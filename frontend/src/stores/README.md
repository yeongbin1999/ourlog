# Zustand 전역 상태 관리

이 프로젝트는 Zustand를 사용한 전역 상태 관리 시스템을 구현했습니다.

## 📁 구조

```
src/stores/
├── authStore.ts      # 인증 상태 관리
├── apiStore.ts       # API 호출 상태 관리
├── uiStore.ts        # UI 상태 관리
├── index.ts          # 스토어 export
└── README.md         # 이 파일
```

## 🚀 사용법

### 1. 인증 스토어 (useAuthStore)

```typescript
import { useAuthStore } from '../stores';

function MyComponent() {
  const { user, isAuthenticated, login, logout } = useAuthStore();
  
  const handleLogin = async () => {
    const result = await login({ email: 'test@example.com', password: 'password' });
    if (result.success) {
      console.log('로그인 성공!');
    }
  };
  
  return (
    <div>
      {isAuthenticated ? (
        <p>안녕하세요, {user?.nickname}님!</p>
      ) : (
        <button onClick={handleLogin}>로그인</button>
      )}
    </div>
  );
}
```

### 2. UI 스토어 (useUIStore)

```typescript
import { useUIStore } from '../stores';

function MyComponent() {
  const { theme, setTheme, showToast, sidebarOpen, toggleSidebar } = useUIStore();
  
  return (
    <div>
      <button onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
        테마 변경
      </button>
      <button onClick={() => showToast('성공!', 'success')}>
        토스트 메시지
      </button>
      <button onClick={toggleSidebar}>
        사이드바 {sidebarOpen ? '닫기' : '열기'}
      </button>
    </div>
  );
}
```

### 3. API 스토어

#### 미리 정의된 스토어 사용
```typescript
import { useDiaryApiStore, useUserApiStore } from '../stores';

function MyComponent() {
  const { data: diaries, loading, execute: fetchDiaries } = useDiaryApiStore();
  const { data: user, execute: fetchUser } = useUserApiStore();
  
  useEffect(() => {
    fetchDiaries({
      url: '/api/v1/diaries',
      method: 'GET',
    });
  }, []);
  
  return (
    <div>
      {loading ? '로딩 중...' : (
        <div>{diaries?.map(diary => <div key={diary.id}>{diary.title}</div>)}</div>
      )}
    </div>
  );
}
```

#### 동적 스토어 생성
```typescript
import { createApiStore } from '../stores';

function MyComponent() {
  const customApiStore = createApiStore('custom-api');
  const { data, loading, execute } = customApiStore();
  
  const handleFetch = () => {
    execute({
      url: '/api/v1/custom-endpoint',
      method: 'POST',
      data: { key: 'value' },
    });
  };
  
  return (
    <button onClick={handleFetch} disabled={loading}>
      {loading ? '로딩 중...' : '데이터 가져오기'}
    </button>
  );
}
```

## 🔄 기존 훅과의 호환성

기존 `useAuth` 훅과 동일한 인터페이스를 제공하는 새로운 훅들도 제공합니다:

```typescript
// 기존 방식 (로컬 상태)
import { useAuth } from '../hooks/useAuth';

// 새로운 방식 (전역 상태)
import { useAuth as useAuthZustand } from '../hooks/useAuthZustand';
```

## 🎯 주요 장점

1. **전역 상태 관리**: 모든 컴포넌트에서 동일한 상태 공유
2. **타입 안전성**: TypeScript 완벽 지원
3. **성능 최적화**: 필요한 부분만 리렌더링
4. **개발자 경험**: 간단한 API, DevTools 지원
5. **지속성**: localStorage 자동 동기화
6. **탭 간 동기화**: 여러 탭에서 상태 동기화

## 🔧 설정

### 환경 변수
```env
NEXT_PUBLIC_API_URL=http://localhost:3001
```

### API 인터셉터
- 요청 시 자동으로 토큰 추가
- 401 에러 시 자동 토큰 갱신
- 에러 처리 및 로그아웃 자동화

## 📝 예시 컴포넌트

`src/components/examples/ZustandExample.tsx`에서 모든 기능의 사용 예시를 확인할 수 있습니다.

## 🔄 마이그레이션 가이드

기존 코드에서 Zustand로 마이그레이션하려면:

1. `useAuth()` → `useAuthStore()`
2. `useApi()` → `useDiaryApiStore()` (또는 적절한 스토어)
3. 로컬 상태 → `useUIStore()`

기존 훅들은 그대로 유지되므로 점진적으로 마이그레이션할 수 있습니다. 