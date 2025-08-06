'use client';

import React, { useEffect, useState } from 'react';
import { axiosInstance } from '@/lib/api-client';

type FollowUser = {
  userId: number;
};

type Props = {
  userId: string;
  userType?: 'sent' | 'received' | 'profile' | 'followers' | 'following';
  followId?: number;
  onActionCompleted?: () => void;
  isFollowing?: boolean;
};

type UserProfile = {
  email: string;
  nickname: string;
  profileImageUrl: string;
  bio: string;
};

export default function UserProfileCard({
  userId,
  userType = 'profile',
  followId,
  onActionCompleted,
  isFollowing: isFollowingProp,
}: Props) {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isFollowing, setIsFollowing] = useState<boolean>(
    typeof isFollowingProp === 'boolean' ? isFollowingProp : false
  );
  const [loading, setLoading] = useState(false);
  const [myUserId, setMyUserId] = useState<number | null>(null);

  useEffect(() => {
    const storedId = localStorage.getItem('userId');
    if (storedId) {
      setMyUserId(Number(storedId));
    }
  }, []);

  useEffect(() => {
    axiosInstance
      .get(`/api/v1/users/${userId}`)
      .then((res) => setProfile(res.data.data))
      .catch(() => setError('존재하지 않는 사용자입니다.'));
  }, [userId]);

  const fetchFollowingStatus = async () => {
    if (!myUserId) return;
    try {
      const res = await axiosInstance.get(`/api/v1/follows/followings?userId=${myUserId}`);
      // API 응답 구조에 맞게 data 위치 조정
      const followingList: FollowUser[] = Array.isArray(res.data) ? res.data : res.data.data ?? [];
      const isMeFollowing = followingList.some(user => user.userId === Number(userId));
      setIsFollowing(isMeFollowing);
    } catch (err) {
      console.error('팔로잉 상태 불러오기 실패', err);
    }
  };

  useEffect(() => {
    if (typeof isFollowingProp === 'boolean') {
      setIsFollowing(isFollowingProp);
    } else if (['profile', 'followers', 'following'].includes(userType)) {
      fetchFollowingStatus();
    }
  }, [isFollowingProp, userType, myUserId, userId]);

  const toggleFollow = async () => {
    setLoading(true);

    try {
      const url = `/api/v1/follows/${userId}`;

      if (isFollowing) {
        await axiosInstance.delete(url);
      } else {
        await axiosInstance.post(url);
      }

      alert(isFollowing ? '언팔로우 완료!' : '팔로우 요청 완료!');
      await fetchFollowingStatus();
      onActionCompleted?.();
    } catch (err) {
      console.error('팔로우 요청 실패', err);
      alert('요청 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const acceptFollow = async () => {
    if (!followId) return;

    setLoading(true);
    try {
      const res = await axiosInstance.post(`/api/v1/follows/${followId}/accept`);
      if (res.status < 200 || res.status >= 300) throw new Error('서버 오류');

      alert('수락 완료!');
      await fetchFollowingStatus();
      onActionCompleted?.();
    } catch (err) {
      console.error('수락 실패', err);
      alert('수락 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const rejectFollow = async () => {
    if (!followId) return;

    setLoading(true);
    try {
      const res = await axiosInstance.delete(`/api/v1/follows/${followId}/reject`);
      if (res.status < 200 || res.status >= 300) throw new Error('서버 오류');

      alert('거절 완료!');
      onActionCompleted?.();
    } catch (err) {
      console.error('거절 실패', err);
      alert('거절 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const renderActionButton = () => {
    if (loading) {
      return (
        <button disabled className="mt-6 px-4 py-2 bg-gray-200 rounded">
          처리 중...
        </button>
      );
    }

    switch (userType) {
      case 'received':
        return (
          <div className="flex gap-2 mt-6">
            <button
              onClick={acceptFollow}
              className="px-4 py-2 bg-green-500 text-white rounded"
            >
              수락
            </button>
            <button
              onClick={rejectFollow}
              className="px-4 py-2 bg-red-500 text-white rounded"
            >
              거절
            </button>
          </div>
        );
      case 'sent':
        return <span className="mt-6 text-sm text-gray-500">요청 보냄</span>;
      default:
        return (
          <button
            onClick={toggleFollow}
            className={`mt-6 px-4 py-2 border rounded-md transition ${
              isFollowing
                ? 'bg-gray-200 text-black hover:bg-gray-300'
                : 'border-black hover:bg-black hover:text-white'
            }`}
          >
            {isFollowing ? '언팔로우' : '팔로우'}
          </button>
        );
    }
  };

  if (error) return <div className="text-center text-black text-lg mt-10">{error}</div>;
  if (!profile) return <div className="text-center">⏳ 로딩 중...</div>;

  return (
    <div className="w-full max-w-sm bg-white p-6 rounded-3xl shadow-md border border-black mx-auto flex flex-col items-center text-center">
      <div
        className="w-20 h-20 mb-4 rounded-full bg-center bg-cover"
        style={{ backgroundImage: `url(${profile.profileImageUrl})` }}
      />
      <h2 className="text-2xl font-bold mb-1">{profile.nickname}</h2>
      <p className="text-sm text-gray-600 mb-2">{profile.bio}</p>
      <hr className="my-4 w-full" />
      <ul className="space-y-2 text-sm text-gray-600 w-full text-left pl-4 ml-28">
        <li>Email: {profile.email}</li>
      </ul>
      {renderActionButton()}
    </div>
  );
}