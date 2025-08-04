"use client";

import React, { useEffect, useState } from "react";

export default function Home() {
  const [visibleElements, setVisibleElements] = useState(new Set());

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setVisibleElements((prev) => new Set([...prev, entry.target.id]));
          }
        });
      },
      { threshold: 0.1 }
    );

    const elements = document.querySelectorAll("[data-fade]");
    elements.forEach((el) => observer.observe(el));

    return () => observer.disconnect();
  }, []);

  const handleCTAClick = () => {
    alert("회원가입/로그인 페이지로 이동합니다!");
  };

  const handleLearnMore = () => {
    document.getElementById("features")?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <div className="bg-gray-50 text-gray-900">
      {/* 히어로 섹션 */}
      <section className="bg-gradient-to-b from-white to-gray-50 min-h-screen flex items-center">
        <div className="max-w-6xl mx-auto px-6">
          <div className="grid lg:grid-cols-2 gap-20 items-center">
            <div>
              <h1 className="text-5xl lg:text-6xl font-black text-gray-900 mb-6 leading-tight tracking-tight">
                당신의 감상을<br />기록하세요
              </h1>
              <p className="text-xl text-gray-600 mb-10 leading-relaxed">
                영화, 책, 드라마, 음악까지.<br />
                모든 콘텐츠를 하나의 감상일기로 관리하세요.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button
                  onClick={handleCTAClick}
                  className="bg-gray-900 text-white px-8 py-4 rounded-lg text-base font-semibold hover:bg-gray-700 transition-all duration-200 hover:-translate-y-0.5 shadow-sm hover:shadow-lg"
                >
                  지금 시작하기
                </button>
                <button
                  onClick={handleLearnMore}
                  className="bg-transparent text-gray-600 px-8 py-4 border-2 border-gray-300 rounded-lg text-base font-semibold hover:border-gray-900 hover:text-gray-900 transition-all duration-200"
                >
                  더 알아보기
                </button>
              </div>
            </div>
            
            <div className="flex justify-center items-center">
              <div className="relative w-80 h-96">
                {/* 미리보기 카드들 */}
                <div className="absolute top-0 left-0 w-70 h-80 bg-white rounded-2xl border border-gray-200 p-6 shadow-lg transform -rotate-2 hover:rotate-0 hover:scale-105 transition-all duration-300 z-30">
                  <div className="mb-4">
                    <span className="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs font-semibold">책</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mb-3">미드나잇 라이브러리</h3>
                  <p className="text-sm text-gray-600 mb-4 leading-relaxed">
                    인생의 선택들에 대해 깊이 생각하게 만드는 책이었다. 후회와 가능성이라는 주제를 판타지적 설정으로 풀어낸 것이 인상적...
                  </p>
                  <div className="flex items-center gap-2 mt-auto">
                    <span className="text-yellow-400">★★★★★</span>
                    <span className="text-sm text-gray-600">5.0</span>
                  </div>
                </div>
                
                <div className="absolute top-5 right-0 w-70 h-80 bg-white rounded-2xl border border-gray-200 p-6 shadow-lg transform rotate-3 hover:rotate-0 hover:scale-105 transition-all duration-300 z-20">
                  <div className="mb-4">
                    <span className="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs font-semibold">영화</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mb-3">라라랜드</h3>
                  <p className="text-sm text-gray-600 mb-4 leading-relaxed">
                    음악과 영상미가 정말 아름다웠다. 현실과 꿈 사이에서 고민하는 모습이 너무 공감됐고, 엔딩이 여운이 길게 남는다...
                  </p>
                  <div className="flex items-center gap-2 mt-auto">
                    <span className="text-yellow-400">★★★★☆</span>
                    <span className="text-sm text-gray-600">4.2</span>
                  </div>
                </div>
                
                <div className="absolute bottom-0 left-5 w-70 h-80 bg-white rounded-2xl border border-gray-200 p-6 shadow-lg transform -rotate-1 hover:rotate-0 hover:scale-105 transition-all duration-300 z-10">
                  <div className="mb-4">
                    <span className="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs font-semibold">음악</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mb-3">Stay - The Kid LAROI</h3>
                  <p className="text-sm text-gray-600 mb-4 leading-relaxed">
                    중독성 있는 멜로디와 감성적인 가사가 너무 좋다. 특히 후렴구 부분이 계속 귀에 맴돌고, 드라이브할 때 듣기 완벽...
                  </p>
                  <div className="flex items-center gap-2 mt-auto">
                    <span className="text-yellow-400">★★★★☆</span>
                    <span className="text-sm text-gray-600">4.5</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 주요 기능 섹션 */}
      <section id="features" className="py-32 bg-white">
        <div className="max-w-6xl mx-auto px-6">
          <div 
            className={`text-center mb-20 transition-all duration-700 ${
              visibleElements.has('features-header') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
            }`}
            data-fade
            id="features-header"
          >
            <h2 className="text-4xl font-black text-gray-900 mb-4 tracking-tight">주요 기능</h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              OurLog와 함께 당신만의 감상 세계를 체계적으로 관리하세요
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 gap-8">
            {[
              { icon: "✍️", title: "감상일기 작성", desc: "영화, 책, 드라마, 음악에 대한 당신의 생각과 감정을 자유롭게 기록하고 별점으로 평가해보세요." },
              { icon: "🔍", title: "콘텐츠 탐색", desc: "다양한 장르의 콘텐츠를 쉽게 검색하고 발견하세요. 개인화된 추천으로 새로운 작품을 만나보세요." },
              { icon: "👥", title: "친구와 감상 공유", desc: "친구들과 감상을 공유하고 서로의 취향을 발견해보세요. 함께 이야기하며 새로운 관점을 얻어보세요." },
              { icon: "📊", title: "통계 보기", desc: "당신의 감상 패턴과 취향을 한눈에 확인하세요. 월별, 장르별 분석으로 나만의 취향을 발견해보세요." }
            ].map((feature, index) => (
              <div
                key={index}
                className={`bg-gray-50 border border-gray-200 rounded-2xl p-10 hover:bg-white hover:border-gray-300 hover:-translate-y-1 hover:shadow-lg transition-all duration-300 ${
                  visibleElements.has(`feature-${index}`) ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
                }`}
                data-fade
                id={`feature-${index}`}
              >
                <div className="w-12 h-12 bg-gray-900 text-white rounded-xl flex items-center justify-center text-xl mb-5">
                  {feature.icon}
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">{feature.title}</h3>
                <p className="text-gray-600 leading-relaxed">{feature.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 사용 흐름 섹션 */}
      <section className="py-32 bg-gray-50">
        <div className="max-w-4xl mx-auto px-6">
          <div 
            className={`text-center mb-16 transition-all duration-700 ${
              visibleElements.has('workflow-header') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
            }`}
            data-fade
            id="workflow-header"
          >
            <h2 className="text-4xl font-black text-gray-900 mb-4 tracking-tight">간단한 3단계</h2>
            <p className="text-lg text-gray-600">누구나 쉽게 시작할 수 있는 감상 기록</p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-12">
            {[
              { num: "1", title: "검색", desc: "보고 싶은 영화, 읽고 싶은 책, 들어본 음악을 검색해보세요" },
              { num: "2", title: "감상", desc: "작품에 대한 감상과 평점을 자유롭게 기록해보세요" },
              { num: "3", title: "공유", desc: "친구들과 감상을 공유하고 새로운 작품을 추천받아보세요" }
            ].map((step, index) => (
              <div
                key={index}
                className={`text-center transition-all duration-700 ${
                  visibleElements.has(`step-${index}`) ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
                }`}
                data-fade
                id={`step-${index}`}
              >
                <div className="w-16 h-16 bg-gray-900 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-6">
                  {step.num}
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">{step.title}</h3>
                <p className="text-gray-600 leading-relaxed">{step.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 미리보기 섹션 */}
      <section className="py-32 bg-white">
        <div className="max-w-6xl mx-auto px-6">
          <div 
            className={`text-center mb-16 transition-all duration-700 ${
              visibleElements.has('showcase-header') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
            }`}
            data-fade
            id="showcase-header"
          >
            <h2 className="text-4xl font-black text-gray-900 mb-4 tracking-tight">실제 사용 예시</h2>
            <p className="text-lg text-gray-600">다른 사용자들이 어떻게 감상을 기록하는지 살펴보세요</p>
          </div>
          
          <div className="grid md:grid-cols-2 gap-8">
            {[
              {
                user: "김영화",
                role: "영화 애호가",
                avatar: "김",
                title: "🎬 기생충 (2019)",
                content: "봉준호 감독의 연출력이 정말 뛰어났다. 사회적 메시지를 오락적 요소와 완벽하게 조화시킨 작품. 특히 반지하 집의 공간적 메타포가 인상적이었고, 배우들의 연기도 너무 자연스러웠다.",
                rating: "★★★★★ 5.0",
                date: "2024.03.15"
              },
              {
                user: "박독서",
                role: "책벌레",
                avatar: "박",
                title: "📚 코스모스 - 칼 세이건",
                content: "과학을 이렇게 아름답게 표현할 수 있다는 것에 감동받았다. 복잡한 우주의 원리를 일반인도 이해하기 쉽게 설명해주는 세이건의 글솜씨가 대단하다. 특히 '창백한 푸른 점' 부분에서 울컥했다.",
                rating: "★★★★★ 4.8",
                date: "2024.03.12"
              }
            ].map((example, index) => (
              <div
                key={index}
                className={`bg-gray-50 border border-gray-200 rounded-2xl p-8 transition-all duration-700 ${
                  visibleElements.has(`example-${index}`) ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
                }`}
                data-fade
                id={`example-${index}`}
              >
                <div className="flex items-center gap-3 mb-5">
                  <div className="w-10 h-10 bg-gray-900 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                    {example.avatar}
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-gray-900">{example.user}</h4>
                    <p className="text-xs text-gray-600">{example.role}</p>
                  </div>
                </div>
                <div className="mb-4">
                  <h5 className="text-base font-semibold text-gray-900 mb-2">{example.title}</h5>
                  <p className="text-sm text-gray-600 leading-relaxed">{example.content}</p>
                </div>
                <div className="flex justify-between items-center pt-4 border-t border-gray-200 text-sm">
                  <span className="text-yellow-500">{example.rating}</span>
                  <span className="text-gray-500">{example.date}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA 섹션 */}
      <section className="py-32 bg-gray-900 text-center">
        <div className="max-w-4xl mx-auto px-6">
          <h2 className="text-5xl font-black text-white mb-4 tracking-tight">지금 바로 시작하세요</h2>
          <p className="text-lg text-gray-300 mb-10">당신만의 감상 여행을 OurLog와 함께 시작해보세요</p>
          <button
            onClick={handleCTAClick}
            className="bg-white text-gray-900 px-10 py-5 rounded-lg text-lg font-bold hover:bg-gray-100 hover:-translate-y-1 hover:shadow-xl transition-all duration-200"
          >
            감상 시작하기
          </button>
        </div>
      </section>
    </div>
  );
}