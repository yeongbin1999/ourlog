import axios, { AxiosError, InternalAxiosRequestConfig, AxiosInstance, AxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/stores/authStore';

/**
 * @file api-client.ts
 * @description 프로젝트 전역에서 사용될 Axios 인스턴스를 생성하고 설정합니다.
 * @module lib/api-client
 */

// API의 기본 URL을 환경 변수에서 가져오거나, 없을 경우 기본값을 사용합니다.
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || '/';

/**
 * Axios 인스턴스
 *
 * 모든 API 요청은 이 인스턴스를 통해 이루어집니다.
 * `withCredentials: true` 옵션을 통해 요청 시 쿠키를 포함하도록 설정합니다.
 */
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

// Axios 인스턴스를 customInstance라는 이름으로 내보냅니다.
// orval로 생성된 코드가 이 이름으로 인스턴스를 가져오기 때문입니다.
export { axiosInstance };

export const customInstance = <T>(config: AxiosRequestConfig, options?: { request?: AxiosRequestConfig }): Promise<T> => {
  const mergedConfig = { ...config, ...options?.request };
  return axiosInstance(mergedConfig);
};

/**
 * 요청 인터셉터 (Request Interceptor)
 *
 * API 요청을 보내기 전에 특정 작업을 수행합니다.
 * 여기서는 Zustand 스토어에서 액세스 토큰을 가져와 Authorization 헤더에 추가합니다.
 */
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const { accessToken } = useAuthStore.getState();

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

/**
 * 응답 인터셉터 (Response Interceptor)
 *
 * API 응답을 받은 후 특정 작업을 수행합니다.
 * 여기서는 401 (Unauthorized) 에러가 발생했을 때, 토큰 갱신을 시도합니다.
 */
axiosInstance.interceptors.response.use(
  (response) => {
    // 정상적인 응답은 그대로 반환합니다.
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // 401 에러이고, 아직 재시도되지 않은 요청인 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // 재시도 플래그를 설정하여 무한 재시도를 방지합니다.

      // Auth 스토어에서 토큰 갱신 함수를 가져옵니다.
      const { refreshAccessToken, logout } = useAuthStore.getState();

      try {
        // 토큰 갱신을 시도합니다.
        const refreshed = await refreshAccessToken();

        if (refreshed) {
          // 토큰 갱신에 성공하면, 이전 요청을 다시 시도합니다.
          return axiosInstance(originalRequest);
        } else {
          // 갱신에 실패하면 로그아웃 처리합니다.
          await logout();
        }
      } catch (refreshError) {
        // 토큰 갱신 중 에러가 발생하면 로그아웃 처리합니다.
        await logout();
        return Promise.reject(refreshError);
      }
    }

    // 그 외의 에러는 그대로 반환합니다.
    return Promise.reject(error);
  },
);