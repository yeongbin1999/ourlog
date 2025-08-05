import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { AxiosError } from 'axios';
import { getMe } from '@/generated/api/api';
import { axiosInstance } from '@/lib/api-client';

export interface User {
  id: string;
  email: string;
  nickname: string;
  profileImageUrl?: string;
  bio?: string;
  followingsCount?: number;
  followersCount?: number;
}

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  isRefreshing: boolean;
}

interface AuthActions {
  login: (
    credentials: { email: string; password: string },
  ) => Promise<{ success: boolean; error?: string }>;
  logout: () => Promise<void>;
  refreshAccessToken: () => Promise<boolean>;
  updateUser: (userData: Partial<User>) => void;
  setLoading: (loading: boolean) => void;
  clearError: () => void;
  initializeAuth: () => Promise<void>;
}

type AuthStore = AuthState & AuthActions;

let refreshPromise: Promise<boolean> | null = null;

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: true,
      error: null,
      isRefreshing: false,

      login: async (credentials) => {
        set({ isLoading: true, error: null });
        try {
          const response = await axiosInstance.post(
            '/api/v1/auth/login',
            credentials,
            {
              headers: { 'Content-Type': 'application/json' },
              withCredentials: true,
            },
          );
          const { accessToken, user } = response.data;

          let currentUser = user;
          if (!currentUser && accessToken) {
            try {
              const meResponse = await getMe();
              const meData = meResponse.data;
              if (meData) {
                currentUser = {
                  id: meData.userId?.toString() || '',
                  email: meData.email || '',
                  nickname: meData.nickname || '',
                  profileImageUrl: meData.profileImageUrl,
                };
              }
            } catch (meError) {
              console.error('Failed to fetch user profile after login:', meError);
            }
          }

          set({
            user: currentUser,
            accessToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });

          window.localStorage.setItem(
            'authEvent',
            JSON.stringify({
              type: 'login',
              timestamp: Date.now(),
              accessToken,
              user: currentUser,
            }),
          );

          return { success: true };
        } catch (error) {
          const axiosError = error as AxiosError<{ message?: string }>;
          const errorMessage =
            axiosError.response?.data?.message || '로그인에 실패했습니다.';
          set({
            isLoading: false,
            error: errorMessage,
          });
          return { success: false, error: errorMessage };
        }
      },

      logout: async () => {
        set({ isLoading: true });
        try {
          await axiosInstance.post(
            '/api/v1/auth/logout',
            {},
            { withCredentials: true },
          );
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

        window.localStorage.setItem(
          'authEvent',
          JSON.stringify({
            type: 'logout',
            timestamp: Date.now(),
          }),
        );
      },

      refreshAccessToken: async () => {
        if (get().isRefreshing) {
          return refreshPromise!;
        }
        set({ isRefreshing: true });

        refreshPromise = (async () => {
          try {
            const response = await axiosInstance.post(
              '/api/v1/auth/reissue',
              {},
              { withCredentials: true },
            );
            const { accessToken } = response.data;
            set({
              accessToken,
              isAuthenticated: true,
              error: null,
            });
            return true;
          } catch (error) {
            console.error('Token refresh failed:', error);
            await get().logout();
            return false;
          } finally {
            set({ isRefreshing: false });
            refreshPromise = null;
          }
        })();

        return refreshPromise;
      },

      updateUser: (userData) => {
        const { user } = get();
        if (user) {
          set({ user: { ...user, ...userData } });
        }
      },

      setLoading: (loading) => set({ isLoading: loading }),

      clearError: () => set({ error: null }),

      initializeAuth: async () => {
        const { accessToken } = get();

        if (!accessToken) {
          const refreshed = await get().refreshAccessToken();
          if (!refreshed) {
            set({ isLoading: false });
            return;
          }
        } else {
          try {
            await axiosInstance.get('/api/v1/auth/verify', {
              headers: { Authorization: `Bearer ${accessToken}` },
              withCredentials: true,
            });
          } catch (error) {
            const refreshed = await get().refreshAccessToken();
            if (!refreshed) {
              await get().logout();
            }
          }
        }

        if (get().isAuthenticated && !get().user) {
          try {
            const meResponse = await getMe();
            const meData = meResponse.data;
            if (meData) {
              set({
                user: {
                  id: meData.userId?.toString() || '',
                  email: meData.email || '',
                  nickname: meData.nickname || '',
                  profileImageUrl: meData.profileImageUrl,
                },
              });
            }
          } catch (meError) {
            console.error('Failed to fetch user profile during initialization:', meError);
            get().logout();
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
    },
  ),
);

// 탭 간 동기화
if (typeof window !== 'undefined') {
  window.addEventListener('storage', (e) => {
    if (e.key === 'authEvent' && e.newValue) {
      try {
        const event = JSON.parse(e.newValue);
        const lastTimestamp = window.localStorage.getItem(
          'lastAuthEventTimestamp',
        );
        if (lastTimestamp === event.timestamp.toString()) return;
        window.localStorage.setItem(
          'lastAuthEventTimestamp',
          event.timestamp.toString(),
        );

        if (event.type === 'logout') {
          useAuthStore.getState().logout();
        } else if (event.type === 'login') {
          const currentState = useAuthStore.getState();
          if (!currentState.accessToken || !currentState.user) {
            window.location.reload();
          }
        }
      } catch (error) {
        console.error('Failed to parse auth event:', error);
      }
    }
  });
}
