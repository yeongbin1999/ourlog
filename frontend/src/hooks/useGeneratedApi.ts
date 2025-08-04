import { useAuthStore } from '../stores';
import { 
  useGetDiary, 
  useWriteDiary, 
  useUpdateDiary, 
  useDeleteDiary,
  useGetUserProfile,
  useLogin,
  useSignup,
  useLogout,
  useSearchContents
} from '../generated/api/api';
import type { 
  DiaryWriteRequestDto,
  DiaryUpdateRequestDto,
  LoginRequest,
  SignupRequest,
  SearchContentsParams
} from '../generated/model';

// 다이어리 관련 API 훅들
export const useDiaryDetail = (diaryId: number) => {
  return useGetDiary(diaryId, {
    query: {
      enabled: !!diaryId && !!useAuthStore.getState().isAuthenticated,
    }
  });
};

export const useCreateDiary = () => {
  return useWriteDiary({
    mutation: {
      onSuccess: () => {
        // 성공 시 다이어리 목록 캐시 무효화
        // queryClient.invalidateQueries({ queryKey: ['diaries'] });
      },
    },
  });
};

export const useUpdateDiaryMutation = () => {
  return useUpdateDiary({
    mutation: {
      onSuccess: () => {
        // 성공 시 해당 다이어리 캐시 무효화
        // queryClient.invalidateQueries({ queryKey: ['diary', id] });
      },
    },
  });
};

export const useDeleteDiaryMutation = () => {
  return useDeleteDiary({
    mutation: {
      onSuccess: () => {
        // 성공 시 다이어리 목록 캐시 무효화
        // queryClient.invalidateQueries({ queryKey: ['diaries'] });
      },
    },
  });
};

// 사용자 관련 API 훅들
export const useUserProfile = (userId: number) => {
  return useGetUserProfile(userId, {
    query: {
      enabled: !!userId && !!useAuthStore.getState().isAuthenticated,
    }
  });
};

// 인증 관련 API 훅들
export const useLoginMutation = () => {
  return useLogin({
    mutation: {
      onSuccess: (response) => {
        // API 응답에서 토큰 추출하여 상태 업데이트
        const token = (response as any)?.data?.accessToken;
        if (token) {
          useAuthStore.setState({
            accessToken: token,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        }
      },
    },
  });
};

export const useRegister = () => {
  return useSignup({
    mutation: {
      onSuccess: (response) => {
        // API 응답에서 토큰 추출하여 상태 업데이트
        const token = (response as any)?.data?.accessToken;
        if (token) {
          useAuthStore.setState({
            accessToken: token,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        }
      },
    },
  });
};

export const useLogoutMutation = () => {
  return useLogout({
    mutation: {
      onSuccess: () => {
        useAuthStore.getState().logout();
      },
    },
  });
};

// 콘텐츠 검색을 위한 훅
export const useSearchContentsQuery = (params: SearchContentsParams) => {
  return useSearchContents(params, {
    query: {
      enabled: !!useAuthStore.getState().isAuthenticated,
    }
  });
}; 