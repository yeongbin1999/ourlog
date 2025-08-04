import { useCallback } from 'react';
import { createApiStore } from '../stores/apiStore';
import { AxiosRequestConfig } from 'axios';

// 기존 useApi 훅과 동일한 인터페이스를 제공하는 새로운 훅
export function useApi<T = any>(storeName?: string) {
  // storeName이 제공되면 새로운 스토어 생성, 아니면 기본 스토어 사용
  const store = storeName ? createApiStore<T>(storeName) : createApiStore<T>('default-api');
  
  const { data, loading, error, execute, reset, setData, setLoading, setError } = store();

  const executeWithConfig = useCallback(async (config?: AxiosRequestConfig) => {
    return execute(config);
  }, [execute]);

  return {
    data,
    loading,
    error,
    execute: executeWithConfig,
    reset,
    setData,
    setLoading,
    setError,
  };
} 