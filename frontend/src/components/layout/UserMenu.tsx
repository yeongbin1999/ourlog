'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import { User, LogIn, UserPlus, LogOut, Settings, HelpCircle } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { useAuthStore } from '@/stores/authStore';
import { toast } from 'sonner';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

export function UserMenu() {
  const { user, isAuthenticated, logout } = useAuthStore();
  const router = useRouter();

  if (!isAuthenticated) {
    return (
      <DropdownMenu modal={false}>
        <DropdownMenuTrigger asChild>
          <User className="w-5 h-5" />
        </DropdownMenuTrigger>
        <DropdownMenuContent className="bg-white shadow-lg rounded-xl">
          <DropdownMenuItem
            onClick={() => {
              setTimeout(() => router.push('/login'), 0);
            }}
          >
            <div className="flex items-center space-x-2">
              <LogIn className="w-4 h-4" />
              <span>로그인</span>
            </div>
          </DropdownMenuItem>
          <DropdownMenuItem
            onClick={() => {
              setTimeout(() => router.push('/signup'), 0);
            }}
          >
            <div className="flex items-center space-x-2">
              <UserPlus className="w-4 h-4" />
              <span>회원가입</span>
            </div>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    );
  }

  return (
    <DropdownMenu modal={false}>
      <DropdownMenuTrigger asChild>
        <Avatar className="w-8 h-8 cursor-pointer">
          <AvatarImage src={user?.profileImageUrl || ''} alt={user?.nickname || 'User'} />
          <AvatarFallback>{user?.nickname?.charAt(0) || 'U'}</AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="bg-white shadow-lg rounded-xl">
        <div className="px-4 py-2 border-b border-gray-200">
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <div className="text-sm font-medium text-gray-900 truncate overflow-hidden text-ellipsis whitespace-nowrap">
                  {user?.nickname}
                </div>
              </TooltipTrigger>
              <TooltipContent>
                <p>{user?.email}</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <div className="text-xs text-gray-500 truncate overflow-hidden text-ellipsis whitespace-nowrap">
                  {user?.email}
                </div>
              </TooltipTrigger>
              <TooltipContent>
                <p>{user?.email}</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        </div>
        <DropdownMenuItem
          onClick={() => {
            setTimeout(() => router.push('/profile/me'), 0);
          }}
        >
          <div className="flex items-center space-x-2">
            <User className="w-4 h-4" />
            <span>내 정보</span>
          </div>
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={() => {
            setTimeout(() => router.push('/settings'), 0);
          }}
        >
          <div className="flex items-center space-x-2">
            <Settings className="w-4 h-4" />
            <span>설정</span>
          </div>
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={() => {
            setTimeout(() => router.push('/help'), 0);
          }}
        >
          <div className="flex items-center space-x-2">
            <HelpCircle className="w-4 h-4" />
            <span>도움말</span>
          </div>
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={async () => {
            await logout();
            toast('로그아웃 되었습니다');
          }}
        >
          <div className="flex items-center space-x-2 text-red-600">
            <LogOut className="w-4 h-4" />
            <span>로그아웃</span>
          </div>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}