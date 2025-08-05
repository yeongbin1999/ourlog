import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UIState {
  theme: 'light' | 'dark';
  sidebarOpen: boolean;
  modalOpen: boolean;
  modalType: string | null;
  toast: {
    message: string;
    type: 'success' | 'error' | 'warning' | 'info';
    visible: boolean;
  } | null;
  loading: boolean;
  searchTerm: string;
  filters: Record<string, unknown>; // any 대신 unknown 사용
}

interface UIActions {
  setTheme: (theme: 'light' | 'dark') => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  openModal: (type: string) => void;
  closeModal: () => void;
  showToast: (
    message: string,
    type?: 'success' | 'error' | 'warning' | 'info',
  ) => void;
  hideToast: () => void;
  setLoading: (loading: boolean) => void;
  setSearchTerm: (term: string) => void;
  setFilter: (key: string, value: unknown) => void; // any 대신 unknown 사용
  clearFilters: () => void;
  resetUI: () => void;
}

type UIStore = UIState & UIActions;

export const useUIStore = create<UIStore>()(
  persist(
    (set, get) => ({
      // 초기 상태
      theme: 'light',
      sidebarOpen: false,
      modalOpen: false,
      modalType: null,
      toast: null,
      loading: false,
      searchTerm: '',
      filters: {},

      // 테마 설정
      setTheme: (theme) => set({ theme }),

      // 사이드바 토글
      toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
      setSidebarOpen: (open) => set({ sidebarOpen: open }),

      // 모달 관리
      openModal: (type) => set({ modalOpen: true, modalType: type }),
      closeModal: () => set({ modalOpen: false, modalType: null }),

      // 토스트 메시지
      showToast: (message, type = 'info') => {
        set({
          toast: {
            message,
            type,
            visible: true,
          },
        });

        // 3초 후 자동 숨김
        setTimeout(() => {
          get().hideToast();
        }, 3000);
      },
      hideToast: () => set({ toast: null }),

      // 로딩 상태
      setLoading: (loading) => set({ loading }),

      // 검색어 설정
      setSearchTerm: (term) => set({ searchTerm: term }),

      // 필터 설정
      setFilter: (key, value) => 
        set((state) => ({
          filters: {
            ...state.filters,
            [key]: value,
          },
        })),

      // 필터 초기화
      clearFilters: () => set({ filters: {} }),

      // UI 상태 초기화
      resetUI: () => set({
        sidebarOpen: false,
        modalOpen: false,
        modalType: null,
        toast: null,
        loading: false,
        searchTerm: '',
        filters: {},
      }),
    }),
    {
      name: 'ui-storage',
      partialize: (state) => ({
        theme: state.theme,
        filters: state.filters,
      }),
    }
  )
); 