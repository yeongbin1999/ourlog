"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { LogIn, UserPlus } from "lucide-react";
import { UserMenu } from "./UserMenu";

const Header = () => {
  const [hoveredItem, setHoveredItem] = useState<string | null>(null);
  const router = useRouter();

  const leftNavItems = [
    { key: "feed", label: "Feed", href: "/social" },
    { key: "diary", label: "Diary", href: "/diaries/select-type" },
    { key: "statistics", label: "Statistics", href: "/statistics" },
    { key: "mypage", label: "MyPage", href: "/profile/me" }, // 나중에 useAuth 적용
  ];

  return (
    <header className="fixed top-0 left-0 w-full z-50 bg-white border-b border-black h-24">
      <div className="w-full h-full flex justify-between items-center">
        {/* 왼쪽: 로고 + 메뉴 */}
        <div className="flex items-center pl-8 space-x-16">
          <Link href="/" passHref>
            <h1 className="text-5xl font-bold text-black font-logo cursor-pointer">
              OUR LOG
            </h1>
          </Link>

          <nav className="flex items-center space-x-4">
            {leftNavItems.map((item) => (
              <Link key={item.key} href={item.href} passHref>
                <span
                  className={`inline-block text-base px-4 py-2 rounded-full transition duration-200 ${
                    hoveredItem === item.key
                      ? "bg-black text-white"
                      : "text-gray-700 hover:bg-black hover:text-white"
                  }`}
                  onMouseEnter={() => setHoveredItem(item.key)}
                  onMouseLeave={() => setHoveredItem(null)}
                >
                  {item.label}
                </span>
              </Link>
            ))}
          </nav>
        </div>

        {/* 오른쪽: 아이콘 + Write */}
        <div className="flex items-center h-full ml-auto pr-0">
        {/* 검색창 */}
          <div className="mr-4">
            <input
              type="text"
              placeholder="Search user..."
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  router.push(`/search?keyword=${e.currentTarget.value}`);
                }
              }}
              className="px-3 py-1.5 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-black text-sm"
            />
          </div>
          {/* 아이콘 툴팁 */}
          <div className="flex items-center space-x-8 mr-6">
            <UserMenu />
          </div>

          {/* Write 버튼 */}
          <div
            onClick={() => router.push("/diaries/write")}
            className={`ml-6 h-full w-[180px] min-w-[160px] flex justify-center items-center cursor-pointer transition duration-200 ${
              hoveredItem === "write"
                ? "bg-gray-800 text-white"
                : "bg-black text-white"
            }`}
            onMouseEnter={() => setHoveredItem("write")}
            onMouseLeave={() => setHoveredItem(null)}
          >
            <span className="text-md">Write</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
