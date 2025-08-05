'use client';

import React, { useEffect, useState } from 'react';
import FollowRequestList from '@/components/user/FollowRequestList';
import SentRequestList from '@/components/user/SentRequestList';
import FollowingList from '@/components/user/FollowingList';
import FollowerList from '@/components/user/FollowerList';
import UserProfileCard from '@/components/user/UserProfileCard';

const TAB_ITEMS = [
  { key: 'received', label: '받은 요청' },
  { key: 'sent', label: '보낸 요청' },
  { key: 'following', label: '팔로잉' },
  { key: 'followers', label: '팔로워' },
] as const;

type TabKey = typeof TAB_ITEMS[number]['key'];

export default function MyProfilePage() {
  const [selectedTab, setSelectedTab] = useState<TabKey | null>('received');
  const [myUserId, setMyUserId] = useState<number | null>(null);
  const [counts, setCounts] = useState<Record<TabKey, number>>({
    received: 0,
    sent: 0,
    following: 0,
    followers: 0,
  });

  useEffect(() => {
    const storedId = localStorage.getItem('userId');
    if (storedId) {
      setMyUserId(Number(storedId));
    }
  }, []);

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
          Object.entries(endpoints).map(([_, url]) => fetch(url).then((r) => r.json()))
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
    <div className="max-w-2xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold text-center mb-8">내 소셜 정보</h1>

      {myUserId && (
        <div className="bg-white rounded-xl shadow-lg p-6 mb-10">
          <UserProfileCard userId={String(myUserId)} userType="profile" />

          <div className="mt-6 flex justify-around">
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

      {/* 아래에 탭별 콘텐츠 렌더링 */}
      {renderTabContent()}
    </div>
  );
}
