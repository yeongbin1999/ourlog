'use client';

import { useDeviceStore } from '../stores';

export const DeviceInfo = () => {
  const { deviceInfo, regenerateDeviceId } = useDeviceStore();

  return (
    <div className="bg-gray-50 p-4 rounded-lg border">
      <h3 className="text-lg font-semibold mb-4">디바이스 정보</h3>
      
      <div className="space-y-2 text-sm">
        <div className="flex justify-between">
          <span className="font-medium">디바이스 ID:</span>
          <span className="font-mono text-blue-600">{deviceInfo.deviceId}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">플랫폼:</span>
          <span>{deviceInfo.platform}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">화면 해상도:</span>
          <span>{deviceInfo.screenResolution}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">시간대:</span>
          <span>{deviceInfo.timeZone}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">언어:</span>
          <span>{deviceInfo.language}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">쿠키 활성화:</span>
          <span>{deviceInfo.cookieEnabled ? '예' : '아니오'}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="font-medium">온라인 상태:</span>
          <span>{deviceInfo.onLine ? '온라인' : '오프라인'}</span>
        </div>
      </div>
      
      <button
        onClick={regenerateDeviceId}
        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
      >
        디바이스 ID 재생성
      </button>
      
      <div className="mt-4 p-3 bg-gray-100 rounded text-xs">
        <p className="font-medium mb-2">User-Agent:</p>
        <p className="break-all">{deviceInfo.userAgent}</p>
      </div>
    </div>
  );
}; 