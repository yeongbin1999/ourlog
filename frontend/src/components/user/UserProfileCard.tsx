'use client';

import React, { useEffect, useState } from 'react';
import { axiosInstance } from '@/lib/api-client';

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
  const [isFollowing, setIsFollowing] = useState<boolean>(!!isFollowingProp);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    axiosInstance
      .get(`/api/v1/users/${userId}`)
      .then((res) => setProfile(res.data.data))
      .catch(() => setError('존재하지 않는 사용자입니다.'));
  }, [userId]);

  useEffect(() => {
    if (typeof isFollowingProp === 'boolean') {
      setIsFollowing(isFollowingProp);
    }
  }, [isFollowingProp]);

  // 팔로우 요청 보내기 (팔로워 목록에서만 사용)
  const sendFollowRequest = async () => {
    if (loading) return;
    setLoading(true);

    try {
      const targetId = Number(userId);
      if (isNaN(targetId)) {
        alert('잘못된 사용자 ID입니다.');
        setLoading(false);
        return;
      }

      await axiosInstance.post(`/api/v1/follows/${targetId}`);
      alert('팔로우 요청을 보냈습니다.');
      onActionCompleted?.();
      // 팔로워 목록에서는 버튼 상태 변하지 않음
    } catch (err: any) {
      if (err.response?.data?.resultCode === 'FOLLOW_001') {
        alert('이미 팔로우한 사용자입니다.');
      } else {
        console.error('팔로우 요청 실패', err);
        alert('요청 처리 중 오류가 발생했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  // 언팔로우 (팔로잉 목록 및 프로필에서 사용)
  const handleUnfollow = async () => {
    if (loading) return;
    setLoading(true);

    try {
      const targetId = Number(userId);
      if (isNaN(targetId)) {
        alert('잘못된 사용자 ID입니다.');
        setLoading(false);
        return;
      }

      await axiosInstance.delete(`/api/v1/follows/${targetId}`);
      alert('언팔로우 완료!');
      setIsFollowing(false);
      onActionCompleted?.();
    } catch (err) {
      console.error('언팔로우 요청 실패', err);
      alert('요청 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 기존 수락/거절 함수들
  const acceptFollow = async () => {
    if (!followId) return;

    setLoading(true);
    try {
      const res = await axiosInstance.post(`/api/v1/follows/${followId}/accept`);
      if (res.status < 200 || res.status >= 300) throw new Error('서버 오류');

      alert('수락 완료!');
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
            <button onClick={acceptFollow} className="px-4 py-2 bg-green-500 text-white rounded">
              수락
            </button>
            <button onClick={rejectFollow} className="px-4 py-2 bg-red-500 text-white rounded">
              거절
            </button>
          </div>
        );
      case 'sent':
        return <span className="mt-6 text-sm text-gray-500">요청 보냄</span>;

      case 'followers':
        // 팔로워 목록에서는 무조건 팔로우 요청 보내기 버튼, 상태 변하지 않음
        return (
          <button
            onClick={sendFollowRequest}
            className="mt-6 px-4 py-2 border rounded-md border-black hover:bg-black hover:text-white transition"
          >
            팔로우 요청 보내기
          </button>
        );

      case 'following':
        // 팔로잉 목록에서는 무조건 언팔로우 버튼
        return (
          <button
            onClick={handleUnfollow}
            className="mt-6 px-4 py-2 border rounded-md bg-gray-200 text-black hover:bg-gray-300 transition"
          >
            언팔로우
          </button>
        );

      default:
        // 기본 (프로필 등) - 팔로잉 상태면 언팔로우, 아니면 팔로우
        return isFollowing ? (
          <button
            onClick={handleUnfollow}
            className="mt-6 px-4 py-2 border rounded-md bg-gray-200 text-black hover:bg-gray-300 transition"
          >
            언팔로우
          </button>
        ) : (
          <button
            onClick={sendFollowRequest}
            className="mt-6 px-4 py-2 border rounded-md border-black hover:bg-black hover:text-white transition"
          >
            팔로우
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