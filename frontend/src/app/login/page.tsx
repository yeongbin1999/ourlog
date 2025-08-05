'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import { useAuthStore } from '@/stores/authStore';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();

  // authStore에서 login 함수와 isLoading 상태를 가져옵니다.
  const { login: authStoreLogin } = useAuthStore();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // authStore의 login 함수를 호출하고 결과를 받습니다.
    const result = await authStoreLogin({ email, password });

    if (result.success) {
      // 로그인 성공 시
      toast.success('로그인 성공!', { duration: 5000 });
      router.push('/');
    } else {
      // 로그인 실패 시
      toast.error(result.error || '로그인 중 오류가 발생했습니다.', {
        duration: 5000,
      });
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-lg bg-white p-8 shadow-md">
        <h2 className="mb-6 text-center text-3xl font-bold text-gray-900">로그인</h2>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              이메일
            </label>
            <Input
              id="email"
              name="email"
              type="email"
              autoComplete="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 block w-full"
            />
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              비밀번호
            </label>
            <Input
              id="password"
              name="password"
              type="password"
              autoComplete="current-password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 block w-full"
            />
          </div>
          <Button type="submit" className="w-full" variant="black">
            로그인
          </Button>
        </form>
        <p className="mt-6 text-center text-sm text-gray-600">
          계정이 없으신가요? {' '}
          <Link href="/signup" className="font-medium text-indigo-600 hover:text-indigo-500">
            회원가입
          </Link>
        </p>
      </div>
    </div>
  );
}