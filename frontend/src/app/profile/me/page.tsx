'use client';

import React, { useEffect, useState } from 'react';
import FollowRequestList from '@/components/user/FollowRequestList';
import SentRequestList from '@/components/user/SentRequestList';
import FollowingList from '@/components/user/FollowingList';
import FollowerList from '@/components/user/FollowerList';
import { useAuthStore } from '@/stores/authStore';

const TAB_ITEMS = [
  { key: 'received', label: '받은 요청' },
  { key: 'sent', label: '보낸 요청' },
  { key: 'following', label: '팔로잉' },
  { key: 'followers', label: '팔로워' },
] as const;

type TabKey = typeof TAB_ITEMS[number]['key'];

export default function MyProfilePage() {
  const [selectedTab, setSelectedTab] = useState<TabKey | null>('received');
  const { user } = useAuthStore();
  console.log('MyProfilePage: user from authStore:', user);
  const [myUserId, setMyUserId] = useState<number | null>(null);
  const [counts, setCounts] = useState<Record<TabKey, number>>({
    received: 0,
    sent: 0,
    following: 0,
    followers: 0,
  });

  useEffect(() => {
    if (user?.id) {
      setMyUserId(Number(user.id));
    }
  }, [user]);

  useEffect(() => {
    if (!myUserId) return;

    const fetchCounts = async () => {
      try {
        const endpoints = {
          received: `${process.env.NEXT_PUBLIC_API_BASE_URL}follows/requests?userId=${myUserId}`,
          sent: `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/sent-requests?userId=${myUserId}`,
          following: `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/followings?userId=${myUserId}`,
          followers: `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/follows/followers?userId=${myUserId}`,
        };

        const res = await Promise.all(
          Object.values(endpoints).map((url) => fetch(url).then((r) => r.json()))
        );

        setCounts({
          received: res[0].length,
          sent: res[1].length,
          following: res[2].length,
          followers: res[3].length,
        });
      } catch (err) {
        console.error('수량 불러오기 실패', err);
      }
    };

    fetchCounts();
  }, [myUserId]);

  const renderTabContent = () => {
    if (!myUserId || selectedTab === null) {
      return <div className="text-center text-gray-500">탭을 클릭하세요!..</div>;
    }

    switch (selectedTab) {
      case 'received':
        return <FollowRequestList myUserId={myUserId} />;
      case 'sent':
        return <SentRequestList myUserId={myUserId} />;
      case 'following':
        return <FollowingList myUserId={myUserId} />;
      case 'followers':
        return <FollowerList myUserId={myUserId} />;
      default:
        return null;
    }
  };

  return (
    <div className="flex">
      <div className="flex-1 px-4 py-10">
        <h1 className="text-3xl font-bold text-center mb-8">내 프로필</h1>

        {myUserId && (
          <div className="bg-white rounded-xl shadow-lg p-6 mb-10">
            {!user ? (
              <div className="text-center">⏳ 프로필 로딩 중...</div>
            ) : (
              <div className="w-full bg-white p-6 rounded-3xl shadow-md border border-black mx-auto flex flex-row items-center gap-6">
                {/* 왼쪽: 프로필 이미지 */}
                <div
                  className="w-24 h-24 rounded-full bg-center bg-cover border border-gray-300"
                  style={{
                    backgroundImage: `url(${user.profileImageUrl || '/images/no-image.png'})`,
                  }}
                />

                {/* 오른쪽: 프로필 정보 */}
                <div className="flex-1">
                  <h2 className="text-2xl font-bold mb-1">{user.nickname}</h2>
                  <p className="text-sm text-gray-600 mb-3">
                    {user.bio || '소개글이 없습니다.'}
                  </p>

                  <div className="flex gap-4 text-sm text-gray-800">
                    <div>
                      <span className="font-semibold">{user.followingsCount ?? 0}</span> 팔로잉
                    </div>
                    <div>
                      <span className="font-semibold">{user.followersCount ?? 0}</span> 팔로워
                    </div>
                  </div>

                  <div className="mt-2 text-xs text-gray-500">📧 {user.email}</div>
                </div>
              </div>
            )}

            {/* 탭 버튼 */}
            <div className="mt-6 flex flex-wrap justify-around gap-3">
              {TAB_ITEMS.map((tab) => (
                <button
                  key={tab.key}
                  onClick={() =>
                    setSelectedTab((prev) => (prev === tab.key ? null : tab.key))
                  }
                  className={`relative px-4 py-2 rounded-md text-sm font-medium transition border ${
                    selectedTab === tab.key
                      ? 'bg-black text-white'
                      : 'bg-white text-black border-gray-300 hover:bg-gray-100'
                  }`}
                >
                  {tab.label}
                  <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
                    {counts[tab.key]}
                  </span>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* 탭 콘텐츠 */}
        {renderTabContent()}
      </div>
      <div className="flex-1 px-4 py-10">
        <h1 className="text-3xl font-bold text-center mb-8">내 프로필</h1>

        {myUserId && (
          <div className="bg-white rounded-xl shadow-lg p-6 mb-10">
            {!user ? (
              <div className="text-center">⏳ 프로필 로딩 중...</div>
            ) : (
              <div className="w-full bg-white p-6 rounded-3xl shadow-md border border-black mx-auto flex flex-row items-center gap-6">
                {/* 왼쪽: 프로필 이미지 */}
                <div
                  className="w-24 h-24 rounded-full bg-center bg-cover border border-gray-300"
                  style={{
                    backgroundImage: `url(${user.profileImageUrl || '/images/no-image.png'})`,
                  }}
                />

                {/* 오른쪽: 프로필 정보 */}
                <div className="flex-1">
                  <h2 className="text-2xl font-bold mb-1">{user.nickname}</h2>
                  <p className="text-sm text-gray-600 mb-3">
                    {user.bio || '소개글이 없습니다.'}
                  </p>

                  <div className="flex gap-4 text-sm text-gray-800">
                    <div>
                      <span className="font-semibold">{user.followingsCount ?? 0}</span> 팔로잉
                    </div>
                    <div>
                      <span className="font-semibold">{user.followersCount ?? 0}</span> 팔로워
                    </div>
                  </div>

                  <div className="mt-2 text-xs text-gray-500">📧 {user.email}</div>
                </div>
              </div>
            )}

            {/* 탭 버튼 */}
            <div className="mt-6 flex flex-wrap justify-around gap-3">
              {TAB_ITEMS.map((tab) => (
                <button
                  key={tab.key}
                  onClick={() =>
                    setSelectedTab((prev) => (prev === tab.key ? null : tab.key))
                  }
                  className={`relative px-4 py-2 rounded-md text-sm font-medium transition border ${
                    selectedTab === tab.key
                      ? 'bg-black text-white'
                      : 'bg-white text-black border-gray-300 hover:bg-gray-100'
                  }`}
                >
                  {tab.label}
                  <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
                    {counts[tab.key]}
                  </span>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* 탭 콘텐츠 */}
        {renderTabContent()}
      </div>
    </div>
  );
}