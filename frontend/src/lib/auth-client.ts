import axios, { AxiosRequestConfig } from 'axios';
import { getDeviceId } from './deviceId';

// 인증 전용 API 기본 설정
const authBaseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// 인증 전용 axios 인스턴스 생성
const authAxiosInstance = axios.create({
  baseURL: authBaseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 인증 요청 인터셉터 - 디바이스 정보 자동 추가
authAxiosInstance.interceptors.request.use(
  (config) => {
    // 디바이스 ID 추가
    const deviceId = getDeviceId();
    config.headers['X-Device-ID'] = deviceId;
    
    // User-Agent 정보도 추가
    if (typeof window !== 'undefined') {
      config.headers['X-User-Agent'] = navigator.userAgent;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 인증 전용 API 호출 함수들
export const authApi = {
  // 로그인
  login: async (credentials: { email: string; password: string }) => {
    return authAxiosInstance.post('/api/v1/auth/login', credentials, {
      withCredentials: true,
    });
  },

  // 회원가입
  register: async (userData: { email: string; password: string; nickname: string }) => {
    return authAxiosInstance.post('/api/v1/auth/register', userData, {
      withCredentials: true,
    });
  },

  // 토큰 리이슈
  refreshToken: async () => {
    return authAxiosInstance.post('/api/v1/auth/refresh', {}, {
      withCredentials: true,
    });
  },

  // 로그아웃
  logout: async () => {
    return authAxiosInstance.post('/api/v1/auth/logout', {}, {
      withCredentials: true,
    });
  },

  // 토큰 검증
  verify: async (token: string) => {
    return authAxiosInstance.get('/api/v1/auth/verify', {
      headers: { 'Authorization': `Bearer ${token}` },
      withCredentials: true,
    });
  },
};

export default authAxiosInstance; 