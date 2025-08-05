import { useInfiniteQuery, QueryKey } from '@tanstack/react-query';
import { searchUsers } from '../generated/api/api';
import { SearchUsersParams, UserProfileResponse } from '../generated/model';

interface SearchUsersResponseData {
  content: UserProfileResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export const useSearchUsersInfinite = (params: Omit<SearchUsersParams, 'pageable'>) => {
  return useInfiniteQuery({
    queryKey: ['searchUsers', params.keyword],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await searchUsers({
        keyword: params.keyword,
        pageable: { page: pageParam, size: 10 }, // Pass page and size within pageable object
      });
      // Assuming RsData<Page<UserProfileResponse>> structure
      return {
        content: response.data?.content || [],
        page: response.data?.page || 0,
        size: response.data?.size || 10,
        totalElements: response.data?.totalElements || 0,
        totalPages: response.data?.totalPages || 0,
        hasNext: response.data?.hasNext || false,
      };
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.hasNext) {
        return lastPage.page + 1;
      }
      return undefined;
    },
    initialPageParam: 0,
    enabled: !!params.keyword, // Only enable query if keyword exists
  });
};
