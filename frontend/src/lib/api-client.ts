import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores';
import { getDeviceId } from './deviceId';

// API 기본 설정
const baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000';

// axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 - 토큰 및 디바이스 ID 자동 추가
axiosInstance.interceptors.request.use(
  (config) => {
    // 인증 토큰 추가
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 디바이스 ID 추가 (실무적으로 모든 요청에 포함)
    const deviceId = getDeviceId();
    config.headers['X-Device-ID'] = deviceId;
    
    // User-Agent 정보도 추가 (선택사항)
    if (typeof window !== 'undefined') {
      config.headers['X-User-Agent'] = navigator.userAgent;
    }
    
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
    if (error.response?.status === 401) {
      // 토큰 만료 시 자동 로그아웃
      useAuthStore.getState().logout();
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