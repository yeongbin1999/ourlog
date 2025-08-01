"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { LogIn, UserPlus } from "lucide-react";

const Header = () => {
  const [hoveredItem, setHoveredItem] = useState<string | null>(null);
  const router = useRouter();

  const leftNavItems = [
    { key: "feed", label: "Feed", href: "/" },
    { key: "diary", label: "Diary", href: "/diaries" },
    { key: "statistics", label: "Statistics", href: "/statistics" },
    { key: "mypage", label: "MyPage", href: "/profile/me" },
  ];

  return (
    <header className="fixed top-0 left-0 w-full z-50 bg-white border-b border-black h-24">
      <div className="w-full h-full flex justify-between items-center">
        {/* 왼쪽: 로고 + 메뉴 */}
        <div className="flex items-center pl-8 space-x-16">
          <h1 className="text-2xl font-bold text-black font-logo">OUR LOG</h1>
          <nav className="flex items-center space-x-4">
            {leftNavItems.map((item) => (
              <Link key={item.key} href={item.href} passHref>
                <span
                  className={`inline-block text-base px-4 py-2 rounded-full transition duration-200 ${
                    hoveredItem === item.key
                      ? "bg-black text-white"
                      : "text-gray-700 hover:text-black"
                  }`}
                  style={{ textDecoration: "none", color: "inherit" }}
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
          {/* 아이콘 툴팁 */}
          <div className="flex items-center space-x-8 mr-6">
            <Link href="/signup" passHref>
              <div className="relative group cursor-pointer">
                <UserPlus className="w-6 h-6 text-black" />
                <div className="absolute top-full mt-1 left-1/2 -translate-x-1/2 scale-0 group-hover:scale-100 transition-transform bg-black text-white text-xs px-2 py-1 rounded whitespace-nowrap z-10">
                  SignUp
                </div>
              </div>
            </Link>
            <Link href="/login" passHref>
              <div className="relative group cursor-pointer">
                <LogIn className="w-6 h-6 text-black" />
                <div className="absolute top-full mt-1 left-1/2 -translate-x-1/2 scale-0 group-hover:scale-100 transition-transform bg-black text-white text-xs px-2 py-1 rounded whitespace-nowrap z-10">
                  Login
                </div>
              </div>
            </Link>
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
