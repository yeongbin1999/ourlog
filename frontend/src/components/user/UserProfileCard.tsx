/* eslint-disable @typescript-eslint/no-explicit-any */

'use client';

import React, { useEffect, useState } from 'react';
import axios from 'axios';

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

  // 로그인한 사용자 ID 불러오기..
  useEffect(() => {
    const storedId = localStorage.getItem('userId');
    if (storedId) {
      setMyUserId(Number(storedId));
    }
  }, []);

  // 프로필 데이터 로드..
  useEffect(() => {
    axios
      .get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/users/${userId}`)
      .then((res) => setProfile(res.data))
      .catch(() => setError('존재하지 않는 사용자입니다.'));
  }, [userId]);

  // ..팔로잉 상태 확인
  const fetchFollowingStatus = async () => {
    if (!myUserId) return;
    try {
      const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/followings?userId=${myUserId}`);
      const followingList = res.data;
      const isMeFollowing = followingList.some(
        (user: FollowUser) => user.userId === Number(userId)
      );
      setIsFollowing(isMeFollowing);
    } catch (err) {
      console.error('팔로잉 상태 불러오기 실패', err);
    }
  };

   useEffect(() => {
     if (typeof isFollowingProp === 'boolean') {
       setIsFollowing(isFollowingProp); // props 우선
     } else if (['profile', 'followers', 'following'].includes(userType)) {
       fetchFollowingStatus(); // API로 확인
     }
   }, [isFollowingProp, userType, myUserId, userId]);



  // 팔로우 / 언팔로우
  const toggleFollow = async () => {
    if (!myUserId) return;
    setLoading(true);

    try {
      // 조건에 따라 param 이름만 다르게 처리
      const paramKey = isFollowing ? 'myUserId' : 'followerId';
      const url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/${userId}?${paramKey}=${myUserId}`;
      const method = isFollowing ? 'DELETE' : 'POST';

      await fetch(url, { method });

      const msg = isFollowing ? '언팔로우 완료!' : '팔로우 요청 완료!';
      alert(msg);
      await fetchFollowingStatus();
      window.location.reload();
    } catch (err) {
      console.error('팔로우 요청 실패', err);
      alert('요청 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };


  // 수락 / 거절..
  const acceptFollow = async () => {
    console.log('[디버깅] followId: ', followId);
    if (!followId) return;

    setLoading(true);
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/${followId}/accept`, {
        method: 'POST',
      });

      if (!res.ok) throw new Error('서버 오류');

      alert('수락 완료!');
      await fetchFollowingStatus();

      onActionCompleted?.();
      window.location.reload();
    } catch (err) {
      console.error('수락 실패', err);
    } finally {
      setLoading(false);
    }
  };

  const rejectFollow = async () => {
    if (!followId) return;

    setLoading(true);
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/${followId}/reject`, {
        method: 'DELETE',
      });

      if (!res.ok) throw new Error('서버 오류');

      alert('거절 완료!');
      onActionCompleted?.();
      window.location.reload();
    } catch (err) {
      console.error('거절 실패', err);
    } finally {
      setLoading(false);
    }
  };



  // 버튼 렌더링..
  const renderActionButton = () => {
    if (!myUserId || String(myUserId) === userId) return null;
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
