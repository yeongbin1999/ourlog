import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import axios from 'axios';

interface User {
  id: string;
  email: string;
  nickname: string;
  profileImageUrl?: string;
}

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

interface AuthActions {
  login: (credentials: { email: string; password: string }) => Promise<{ success: boolean; error?: string }>;
  logout: () => Promise<void>;
  refreshAccessToken: () => Promise<boolean>;
  updateUser: (userData: Partial<User>) => void;
  setLoading: (loading: boolean) => void;
  clearError: () => void;
  initializeAuth: () => Promise<void>;
}

type AuthStore = AuthState & AuthActions;

const ACCESS_TOKEN_KEY = 'accessToken';
const USER_KEY = 'user';

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // 초기 상태
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: true,
      error: null,

      // 로그인
      login: async (credentials) => {
        set({ isLoading: true, error: null });
        
        try {
          const response = await axios.post('/api/v1/auth/login', credentials, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true,
          });

          const { accessToken, user } = response.data;
          
          set({
            user,
            accessToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });

          // 다른 탭에 로그인 이벤트 알림
          window.localStorage.setItem('authEvent', JSON.stringify({
            type: 'login',
            timestamp: Date.now()
          }));

          return { success: true };
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || '로그인에 실패했습니다.';
          set({
            isLoading: false,
            error: errorMessage,
          });
          return { success: false, error: errorMessage };
        }
      },

      // 로그아웃
      logout: async () => {
        set({ isLoading: true });

        try {
          // 백엔드에 로그아웃 요청
          await axios.post('/api/v1/auth/logout', {}, {
            withCredentials: true,
          });
        } catch (error) {
          console.error('Logout API error:', error);
        }

        set({
          user: null,
          accessToken: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
        });

        // 다른 탭에 로그아웃 이벤트 알림
        window.localStorage.setItem('authEvent', JSON.stringify({
          type: 'logout',
          timestamp: Date.now()
        }));
      },

      // 토큰 갱신
      refreshAccessToken: async () => {
        try {
          const response = await axios.post('/api/v1/auth/refresh', {}, {
            withCredentials: true,
          });

          const { accessToken } = response.data;
          
          set({
            accessToken,
            isAuthenticated: true,
            error: null,
          });

          return true;
        } catch (error) {
          console.error('Token refresh failed:', error);
          get().logout();
          return false;
        }
      },

      // 사용자 정보 업데이트
      updateUser: (userData) => {
        const { user } = get();
        if (user) {
          set({ user: { ...user, ...userData } });
        }
      },

      // 로딩 상태 설정
      setLoading: (loading) => set({ isLoading: loading }),

      // 에러 초기화
      clearError: () => set({ error: null }),

      // 초기 인증 상태 확인
      initializeAuth: async () => {
        const { accessToken } = get();
        
        if (!accessToken) {
          // 액세스 토큰이 없으면 리프레시 토큰으로 갱신 시도
          const refreshed = await get().refreshAccessToken();
          if (!refreshed) {
            set({ isLoading: false });
            return;
          }
        } else {
          // 액세스 토큰이 있으면 유효성 검사
          try {
            await axios.get('/api/v1/auth/verify', {
              headers: { 'Authorization': `Bearer ${accessToken}` },
              withCredentials: true,
            });
          } catch (error) {
            // 토큰이 유효하지 않으면 리프레시 시도
            const refreshed = await get().refreshAccessToken();
            if (!refreshed) {
              get().logout();
            }
          }
        }

        set({ isLoading: false });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

// 탭 간 동기화를 위한 이벤트 리스너 설정
if (typeof window !== 'undefined') {
  window.addEventListener('storage', (e) => {
    if (e.key === 'authEvent' && e.newValue) {
      try {
        const event = JSON.parse(e.newValue);
        
        // 같은 이벤트는 무시 (중복 처리 방지)
        if (event.timestamp === window.localStorage.getItem('lastAuthEventTimestamp')) {
          return;
        }
        
        window.localStorage.setItem('lastAuthEventTimestamp', event.timestamp.toString());

        if (event.type === 'logout') {
          // 다른 탭에서 로그아웃했으면 현재 탭도 로그아웃
          useAuthStore.getState().logout();
        } else if (event.type === 'login') {
          // 다른 탭에서 로그인했으면 현재 탭의 상태를 동기화
          const currentState = useAuthStore.getState();
          if (!currentState.accessToken || !currentState.user) {
            // 현재 탭에 인증 정보가 없으면 새로고침
            window.location.reload();
          }
        }
      } catch (error) {
        console.error('Failed to parse auth event:', error);
      }
    }
  });
} 