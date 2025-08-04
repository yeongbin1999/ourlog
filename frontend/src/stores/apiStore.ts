import { create } from 'zustand';
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from './authStore';

interface ApiState<T = any> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

interface ApiActions<T = any> {
  execute: (config?: AxiosRequestConfig) => Promise<T | null>;
  reset: () => void;
  setData: (data: T | null) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
}

type ApiStore<T = any> = ApiState<T> & ApiActions<T>;

// 제네릭 API 스토어 생성 함수
export const createApiStore = <T = any>(storeName: string) => {
  return create<ApiStore<T>>((set, get) => ({
    data: null,
    loading: false,
    error: null,

    execute: async (config?: AxiosRequestConfig) => {
      set({ loading: true, error: null });
      
      try {
        if (!config) {
          throw new Error('API config is required');
        }
        
        const response: AxiosResponse<T> = await axios(config);
        
        set({
          data: response.data,
          loading: false,
          error: null,
        });
        
        return response.data;
      } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'An error occurred';
        set({
          data: null,
          loading: false,
          error: errorMessage,
        });
        return null;
      }
    },

    reset: () => {
      set({
        data: null,
        loading: false,
        error: null,
      });
    },

    setData: (data) => set({ data }),
    setLoading: (loading) => set({ loading }),
    setError: (error) => set({ error }),
  }));
};

// 자주 사용하는 API 스토어들 미리 생성
export const useUserApiStore = createApiStore<any>('user-api');
export const useDiaryApiStore = createApiStore<any>('diary-api');
export const useCommentApiStore = createApiStore<any>('comment-api');
export const useStatisticsApiStore = createApiStore<any>('statistics-api');

// 전역 API 설정
export const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || '',
  timeout: 10000,
});

// 요청 인터셉터 - 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    // Zustand 스토어에서 토큰 가져오기
    const { accessToken } = useAuthStore.getState();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 토큰 갱신 처리
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshed = await useAuthStore.getState().refreshAccessToken();
        if (refreshed) {
          const { accessToken } = useAuthStore.getState();
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
      }
    }

    return Promise.reject(error);
  }
); 