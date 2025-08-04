'use client';

import { useState } from 'react';
import { useAuthStore, useUIStore, useDiaryApiStore } from '../../stores';

export function ZustandExample() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Zustand 스토어 사용
  const { user, isAuthenticated, isLoading, login, logout } = useAuthStore();
  const { theme, setTheme, showToast, sidebarOpen, toggleSidebar } = useUIStore();
  const { data: diaries, loading: diariesLoading, execute: fetchDiaries } = useDiaryApiStore();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    const result = await login({ email, password });
    
    if (result.success) {
      showToast('로그인 성공!', 'success');
    } else {
      showToast(result.error || '로그인 실패', 'error');
    }
  };

  const handleFetchDiaries = async () => {
    await fetchDiaries({
      url: '/api/v1/diaries',
      method: 'GET',
    });
  };

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Zustand 전역 상태 관리 예시</h1>
      
      {/* 인증 상태 */}
      <div className="border rounded-lg p-4">
        <h2 className="text-lg font-semibold mb-4">인증 상태</h2>
        <div className="space-y-2">
          <p>인증 상태: {isAuthenticated ? '로그인됨' : '로그아웃됨'}</p>
          <p>로딩: {isLoading ? '로딩 중...' : '완료'}</p>
          {user && (
            <div>
              <p>사용자: {user.nickname} ({user.email})</p>
            </div>
          )}
        </div>

        {!isAuthenticated ? (
          <form onSubmit={handleLogin} className="mt-4 space-y-2">
            <input
              type="email"
              placeholder="이메일"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="border rounded px-3 py-2 w-full"
            />
            <input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="border rounded px-3 py-2 w-full"
            />
            <button
              type="submit"
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              로그인
            </button>
          </form>
        ) : (
          <button
            onClick={logout}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 mt-4"
          >
            로그아웃
          </button>
        )}
      </div>

      {/* UI 상태 */}
      <div className="border rounded-lg p-4">
        <h2 className="text-lg font-semibold mb-4">UI 상태</h2>
        <div className="space-y-2">
          <p>테마: {theme}</p>
          <p>사이드바: {sidebarOpen ? '열림' : '닫힘'}</p>
        </div>
        <div className="mt-4 space-x-2">
          <button
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
            className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
          >
            테마 변경
          </button>
          <button
            onClick={toggleSidebar}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          >
            사이드바 토글
          </button>
          <button
            onClick={() => showToast('테스트 메시지입니다!', 'info')}
            className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600"
          >
            토스트 메시지
          </button>
        </div>
      </div>

      {/* API 상태 */}
      <div className="border rounded-lg p-4">
        <h2 className="text-lg font-semibold mb-4">API 상태</h2>
        <div className="space-y-2">
          <p>다이어리 로딩: {diariesLoading ? '로딩 중...' : '완료'}</p>
          {diaries && (
            <div>
              <p>다이어리 데이터: {JSON.stringify(diaries).slice(0, 100)}...</p>
            </div>
          )}
        </div>
        <button
          onClick={handleFetchDiaries}
          className="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600 mt-4"
        >
          다이어리 가져오기
        </button>
      </div>
    </div>
  );
} 