// 기존 훅들 (로컬 상태)
export { useUrlParams } from './useUrlParams';
export { useApi } from './useApi';
export { useLocalStorage } from './useLocalStorage';
export { useDebounce } from './useDebounce';
export { useAuth } from './useAuth';

// Zustand 기반 훅들 (전역 상태)
export { useAuth as useAuthZustand } from './useAuthZustand';
export { useApi as useApiZustand } from './useApiZustand';

// Zustand 스토어들 직접 export
export { 
  useAuthStore, 
  useUIStore, 
  useUserApiStore, 
  useDiaryApiStore, 
  useCommentApiStore, 
  useStatisticsApiStore,
  createApiStore,
  apiClient 
} from '../stores'; 