import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // 외부 이미지 도메인 허용 설정
  images: {
    domains: [
      "i.scdn.co",              // Spotify 앨범 커버
      "image.tmdb.org",         // TMDB 포스터
      "www.nl.go.kr"            // 국립중앙도서관 도서 이미지
    ],
  },

  // API 프록시 설정 
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: "http://localhost:8080/api/:path*", // Spring Boot 백엔드 주소
      },
    ];
  },
};

export default nextConfig;
