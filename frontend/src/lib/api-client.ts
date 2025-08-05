import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores';
import { getDeviceId } from './deviceId';

// API 기본 설정
const baseURL = 'http://localhost:8080';

// axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config) => { 
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    const deviceId = getDeviceId();
    config.headers['X-Device-ID'] = deviceId;
    
    // if (typeof window !== 'undefined') {
    //   config.headers['X-User-Agent'] = navigator.userAgent;
    // }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 에러 처리
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;

    // 401 에러이고, 토큰 재발급 요청이 아니며, 이미 재시도한 요청이 아닌 경우
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true; // 재시도 플래그 설정
      try {
        const refreshed = await useAuthStore.getState().refreshAccessToken();
        if (refreshed) {
          // 새로운 액세스 토큰으로 원래 요청의 Authorization 헤더 업데이트
          originalRequest.headers.Authorization = `Bearer ${useAuthStore.getState().accessToken}`;
          return axiosInstance(originalRequest); // 원래 요청 재시도
        }
      } catch (refreshError) {
        // 토큰 재발급 실패 시 로그아웃
        useAuthStore.getState().logout();
        return Promise.reject(refreshError);
      }
    }

    // 그 외의 401 에러 또는 다른 에러는 그대로 반환
    if (error.response?.status === 401) {
      useAuthStore.getState().logout(); // 재시도 불가능한 401은 로그아웃
    }
    return Promise.reject(error);
  }
);

// Orval에서 사용할 커스텀 인스턴스
export const customInstance = <T>(
  config: AxiosRequestConfig,
  options?: AxiosRequestConfig,
): Promise<T> => {
  const source = axios.CancelToken.source();
  const promise = axiosInstance({
    ...config,
    ...options,
    cancelToken: source.token,
  }).then(({ data }) => data);

  // @ts-expect-error: Orval generated code requires this to allow cancellation.
  promise.cancel = () => {
    source.cancel('Query was cancelled');
  };

  return promise;
};

export default axiosInstance; 