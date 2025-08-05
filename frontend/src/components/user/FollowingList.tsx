'use client';

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import UserProfileCard from './UserProfileCard';

type Props = {
  myUserId: number;
};

type FollowingUserResponse = {
  userId: number;
  nickname: string;
  email: string;
  bio: string;
  profileImageUrl: string;
};

// 내가 팔로우한 사람들..
export default function FollowingList({ myUserId }: Props) {
  const [following, setFollowing] = useState<FollowingUserResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchFollowing = async () => {
    try {
      const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/followings?userId=${myUserId}`);
      setFollowing(res.data);
    } catch (err) {
      console.error('팔로잉 목록 불러오기 실패', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFollowing();
  }, []);

  if (loading) return <div className="text-center mt-10">로딩 중...</div>;
  if (following.length === 0) return <div className="text-center mt-10">팔로우한 유저가 없습니다.</div>;

  return (
    <div className="space-y-6">
      {following.map((user) => (
        <UserProfileCard
          key={user.userId}
          userId={String(user.userId)}
          userType="following"
          isFollowing={true}
        />
      ))}
    </div>
  );
}
