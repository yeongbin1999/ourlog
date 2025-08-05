import { create } from 'zustand';
import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from './authStore';

// 제네릭 타입을 명확하게 하고, 기본값을 unknown으로 설정
interface ApiState<T = unknown> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

interface ApiActions<T = unknown> {
  execute: (config?: AxiosRequestConfig) => Promise<T | null>;
  reset: () => void;
  setData: (data: T | null) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
}

type ApiStore<T = unknown> = ApiState<T> & ApiActions<T>;

// 제네릭 API 스토어 생성 함수
export const createApiStore = <T = unknown>() => {
  return create<ApiStore<T>>((set) => ({
    data: null,
    loading: false,
    error: null,

    execute: async (config?: AxiosRequestConfig) => {
      set({ loading: true, error: null });

      try {
        if (!config) {
          throw new Error('API config is required');
        }

        const response: AxiosResponse<T> = await apiClient(config); // 수정: 전역 apiClient 사용

        set({
          data: response.data,
          loading: false,
          error: null,
        });

        return response.data;
      } catch (error) {
        const axiosError = error as AxiosError<{ message?: string }>;
        const errorMessage =
          axiosError.response?.data?.message ||
          axiosError.message ||
          'An error occurred';
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

// 자주 사용하는 API 스토어들 미리 생성 (타입을 unknown으로 유지)
export const useUserApiStore = createApiStore<unknown>();
export const useDiaryApiStore = createApiStore<unknown>();
export const useCommentApiStore = createApiStore<unknown>();
export const useStatisticsApiStore = createApiStore<unknown>();


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

// 응답 인터셉터 - 토큰 갱신 처리 (경쟁 상태 방지)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // 토큰 갱신 시도 (경쟁 상태 방지됨)
        const refreshed = await useAuthStore.getState().refreshAccessToken();
        if (refreshed) {
          const { accessToken } = useAuthStore.getState();
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        // 갱신 실패 시 로그아웃 처리
        useAuthStore.getState().logout();
      }
    }

    return Promise.reject(error);
  }
); 