import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { getDeviceId, regenerateDeviceId, getDeviceInfo } from '../lib/deviceId';

interface DeviceInfo {
  deviceId: string;
  userAgent: string;
  screenResolution: string;
  timeZone: string;
  language: string;
  platform: string;
  cookieEnabled: boolean;
  onLine: boolean;
}

interface DeviceState {
  deviceInfo: DeviceInfo;
  isInitialized: boolean;
}

interface DeviceActions {
  initializeDevice: () => void;
  regenerateDeviceId: () => void;
  updateDeviceInfo: () => void;
}

type DeviceStore = DeviceState & DeviceActions;

export const useDeviceStore = create<DeviceStore>()(
  persist(
    (set, get) => ({
      // 초기 상태
      deviceInfo: {
        deviceId: 'temp_id',
        userAgent: 'unknown',
        screenResolution: 'unknown',
        timeZone: 'UTC',
        language: 'en',
        platform: 'unknown',
        cookieEnabled: false,
        onLine: false,
      },
      isInitialized: false,

      // 디바이스 초기화
      initializeDevice: () => {
        const deviceInfo = getDeviceInfo();
        set({
          deviceInfo,
          isInitialized: true,
        });
      },

      // 디바이스 ID 재생성
      regenerateDeviceId: () => {
        const newDeviceId = regenerateDeviceId();
        const currentInfo = get().deviceInfo;
        set({
          deviceInfo: {
            ...currentInfo,
            deviceId: newDeviceId,
          },
        });
      },

      // 디바이스 정보 업데이트
      updateDeviceInfo: () => {
        const deviceInfo = getDeviceInfo();
        set({
          deviceInfo,
        });
      },
    }),
    {
      name: 'device-store',
      partialize: (state) => ({
        deviceInfo: state.deviceInfo,
        isInitialized: state.isInitialized,
      }),
    }
  )
); 