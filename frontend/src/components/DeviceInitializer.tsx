'use client';

import { useEffect } from 'react';
import { useDeviceStore } from '../stores';

interface DeviceInitializerProps {
  children: React.ReactNode;
}

export const DeviceInitializer = ({ children }: DeviceInitializerProps) => {
  const { isInitialized, initializeDevice } = useDeviceStore();

  useEffect(() => {
    if (!isInitialized) {
      initializeDevice();
    }
  }, [isInitialized, initializeDevice]);

  // 디바이스 정보가 초기화되기 전까지는 로딩 상태를 보여줄 수 있음
  if (!isInitialized) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">디바이스 정보를 초기화하는 중...</p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}; 