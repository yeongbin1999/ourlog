import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /*프론트가 /api/*로 요청하면 내부적으로 백엔드(:8080)로 프록시해줍니다..*/
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
