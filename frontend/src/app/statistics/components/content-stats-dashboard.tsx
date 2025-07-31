"use client"

import { useState, useMemo, useEffect } from "react"
import { Calendar, BarChart3, Star, CalendarIcon } from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ChartContainer, ChartTooltip } from "@/components/ui/chart"
import { Line, LineChart, Pie, PieChart, Cell, ResponsiveContainer, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts"
import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar as CalendarComponent } from "@/components/ui/calendar"
import { format } from "date-fns"
import { ko } from "date-fns/locale"


const BASE_URL = "http://localhost:8080/api/v1/statistics";

async function fetchCard(): Promise<StatisticsCardDto> {
    const res = await fetch(`${BASE_URL}/card`);
    return res.json();
}
async function fetchMonthlyDiaryGraph(): Promise<MonthlyDiaryCount[]> {
    const res = await fetch(`${BASE_URL}/monthly-diary-graph`);
    return res.json();
}
async function fetchTypeDistribution(): Promise<TypeCountDto[]> {
    const res = await fetch(`${BASE_URL}/type-distribution`);
    return res.json();
}
async function fetchTypeGraph(period: string): Promise<TypeGraphResponse> {
  // period를 PeriodOption enum 값으로 변환
  const periodMap: Record<string, string> = {
    "전체": "ALL",
    "이번년도": "THIS_YEAR", 
    "최근6개월": "LAST_6_MONTHS",
    "최근한달": "LAST_MONTH",
    "최근일주일": "LAST_WEEK"
  };
  
  const periodOption = periodMap[period] || "ALL";
  
  const res = await fetch(`${BASE_URL}/type-graph?period=${periodOption}`);
  return res.json();
}
async function fetchGenreGraph(period: string): Promise<GenreGraphResponse> {
  const periodMap: Record<string, string> = {
    "전체": "ALL",
    "이번년도": "THIS_YEAR",
    "최근6개월": "LAST_6_MONTHS",
    "최근한달": "LAST_MONTH",
    "최근일주일": "LAST_WEEK"
  };
  const periodOption = periodMap[period] || "ALL";
  const res = await fetch(`${BASE_URL}/genre-graph?period=${periodOption}`);
  return res.json();
}

async function fetchEmotionGraph(period: string): Promise<EmotionGraphResponse> {
  const periodMap: Record<string, string> = {
    "전체": "ALL",
    "이번년도": "THIS_YEAR",
    "최근6개월": "LAST_6_MONTHS",
    "최근한달": "LAST_MONTH",
    "최근일주일": "LAST_WEEK"
  };
  const periodOption = periodMap[period] || "ALL";
  const res = await fetch(`${BASE_URL}/emotion-graph?period=${periodOption}`);
  return res.json();
}

async function fetchOttGraph(period: string): Promise<OttGraphResponse> {
  const periodMap: Record<string, string> = {
    "전체": "ALL",
    "이번년도": "THIS_YEAR",
    "최근6개월": "LAST_6_MONTHS",
    "최근한달": "LAST_MONTH",
    "최근일주일": "LAST_WEEK"
  };
  const periodOption = periodMap[period] || "ALL";
  const res = await fetch(`${BASE_URL}/ott-graph?period=${periodOption}`);
  return res.json();
}

// 새로운 타입 정의
type TypeLineGraphDto = {
  axisLabel: string;
  type: string;
  count: number;
};

type TypeRankDto = {
  type: string;
  totalCount: number;
};

type TypeGraphResponse = {
  typeLineGraph: TypeLineGraphDto[];
  typeRanking: TypeRankDto[];
};

type StatisticsCardDto = {
  totalDiaryCount: number;
  averageRating: number;
  favoriteType: string;
  favoriteTypeCount: number;
  favoriteEmotion: string;
  favoriteEmotionCount: number;
};

type MonthlyDiaryCount = { period: string; views: number };

type TypeCountDto = {
  type: string;
  count: number;
};

// 1. 타입 정의 추가

type GenreLineGraphDto = {
  axisLabel: string;
  genre: string;
  count: number;
};
type GenreRankDto = {
  genre: string;
  totalCount: number;
};
type GenreGraphResponse = {
  genreLineGraph: GenreLineGraphDto[];
  genreRanking: GenreRankDto[];
};

type EmotionLineGraphDto = {
    axisLabel: string;
    emotion: string;
    count: number;
};

type EmotionRankDto = {
    emotion: string;
    totalCount: number;
};

type EmotionGraphResponse = {
    emotionLineGraph: EmotionLineGraphDto[];
    emotionRanking: EmotionRankDto[];
};

// OTT 타입 정의
type OttLineGraphDto = {
  axisLabel: string;
  ottName: string;
  count: number;
};

type OttRankDto = {
  ottName: string;
  totalCount: number;
};

type OttGraphResponse = {
  ottLineGraph: OttLineGraphDto[];
  ottRanking: OttRankDto[];
};


// 월별 데이터 (기본 데이터) - 장르별, 감정별용
const monthlyGenreData = [
    { period: "1월", 드라마: 15, 액션: 10, 로맨스: 8, 공포: 7, 코미디: 6 },
    { period: "2월", 드라마: 14, 액션: 12, 로맨스: 9, 공포: 6, 코미디: 7 },
    { period: "3월", 드라마: 13, 액션: 11, 로맨스: 10, 공포: 5, 코미디: 8 },
    { period: "4월", 드라마: 12, 액션: 9, 로맨스: 11, 공포: 4, 코미디: 9 },
    { period: "5월", 드라마: 11, 액션: 8, 로맨스: 12, 공포: 3, 코미디: 10 },
    { period: "6월", 드라마: 10, 액션: 7, 로맨스: 13, 공포: 2, 코미디: 11 },
]

// 타입별 색상 맵 추가
const typeColors: Record<string, string> = {
  "DRAMA": "#8884d8",
  "MOVIE": "#82ca9d", 
  "BOOK": "#ffc658",
  "MUSIC": "#ff7c7c"
};

// 장르명 정규화 함수


// 장르별 색상 맵 추가
const genreColors: Record<string, string> = {
  "드라마": "#8884d8",
  "액션": "#82ca9d",
  "로맨스": "#ffc658",
  "공포": "#ff7c7c",
  "코미디": "#8dd1e1",
  // 영어 장르명도 추가
  "drama": "#8884d8",
  "action": "#82ca9d",
  "romance": "#ffc658",
  "horror": "#ff7c7c",
  "comedy": "#8dd1e1",
};

const emotionColors: Record<string, string> = {
    "감동": "#ff6b6b",
    "재미": "#4ecdc4",
    "긴장": "#45b7d1",
    "슬픔": "#96ceb4",
    "분노": "#feca57",
    "공포": "#ff9ff3",
};

const ottColors: Record<string, string> = {
  "Netflix": "#E50914",
  "Tving": "#00A6B5",
  "Watcha": "#FF0044",
  "Coupang Play": "#00A5FF",
  "Disney+": "#01147C",
};

// 변환 함수 추가
type ChartDataPoint = {
  period: string;
  [key: string]: string | number;
};

function convertTypeTimeCountsToChartData(timeCounts: TypeLineGraphDto[]): ChartDataPoint[] {
  const periodMap: Record<string, ChartDataPoint> = {};
  const allKeys = new Set<string>();
  const allPeriods = new Set<string>();

  timeCounts.forEach(({ axisLabel, type }) => {
    allPeriods.add(axisLabel);
    allKeys.add(type);
  });

  const sortedPeriods = Array.from(allPeriods).sort();

  sortedPeriods.forEach(period => {
    periodMap[period] = { period };
    allKeys.forEach(key => {
      periodMap[period][key] = 0;
    });
  });

  timeCounts.forEach(({ axisLabel, type, count }) => {
    periodMap[axisLabel][type] = count;
  });

  return Object.values(periodMap);
}

// 장르별 그래프 데이터 변환 함수
function convertGenreLineGraphToChartData(lineGraph: GenreLineGraphDto[]): ChartDataPoint[] {
  const periodMap: Record<string, ChartDataPoint> = {};
  const allKeys = new Set<string>();
  const allPeriods = new Set<string>();

  lineGraph.forEach(({ axisLabel, genre }) => {
    allPeriods.add(axisLabel);
    allKeys.add(genre);
  });

  const sortedPeriods = Array.from(allPeriods).sort();

  sortedPeriods.forEach(period => {
    periodMap[period] = { period };
    allKeys.forEach(key => {
      periodMap[period][key] = 0;
    });
  });

  lineGraph.forEach(({ axisLabel, genre, count }) => {
    periodMap[axisLabel][genre] = count;
  });

  return Object.values(periodMap);
}

function convertEmotionLineGraphToChartData(lineGraph: EmotionLineGraphDto[]): ChartDataPoint[] {
    const periodMap: Record<string, ChartDataPoint> = {};
    const allKeys = new Set<string>();
    const allPeriods = new Set<string>();

    lineGraph.forEach(({ axisLabel, emotion }) => {
        allPeriods.add(axisLabel);
        allKeys.add(emotion);
    });

    const sortedPeriods = Array.from(allPeriods).sort();

    sortedPeriods.forEach(period => {
        periodMap[period] = { period };
        allKeys.forEach(key => {
            periodMap[period][key] = 0;
        });
    });

    lineGraph.forEach(({ axisLabel, emotion, count }) => {
        periodMap[axisLabel][emotion] = count;
    });

    return Object.values(periodMap);
}

function convertOttLineGraphToChartData(lineGraph: OttLineGraphDto[]): ChartDataPoint[] {
  const periodMap: Record<string, ChartDataPoint> = {};
  const allKeys = new Set<string>();
  const allPeriods = new Set<string>();

  lineGraph.forEach(({ axisLabel, ottName }) => {
    allPeriods.add(axisLabel);
    allKeys.add(ottName);
  });

  const sortedPeriods = Array.from(allPeriods).sort();

  sortedPeriods.forEach(period => {
    periodMap[period] = { period };
    allKeys.forEach(key => {
      periodMap[period][key] = 0;
    });
  });

  lineGraph.forEach(({ axisLabel, ottName, count }) => {
    periodMap[axisLabel][ottName] = count;
  });

  return Object.values(periodMap);
}

export default function Component() {
    const [selectedPeriod, setSelectedPeriod] = useState("전체")
    const [startDate, setStartDate] = useState<Date>()
    const [endDate, setEndDate] = useState<Date>()
    const [activeTab, setActiveTab] = useState("overview")
    const [card, setCard] = useState<StatisticsCardDto | null>(null);
    const [monthlyDiary, setMonthlyDiary] = useState<MonthlyDiaryCount[]>([]);
    const [typeDist, setTypeDist] = useState<TypeCountDto[]>([]);
    const [typeGraph, setTypeGraph] = useState<TypeGraphResponse>({ typeLineGraph: [], typeRanking: [] });
    const [genreGraph, setGenreGraph] = useState<GenreGraphResponse>({ genreLineGraph: [], genreRanking: [] });
    const [emotionGraph, setEmotionGraph] = useState<EmotionGraphResponse>({ emotionLineGraph: [], emotionRanking: [] });
    const [ottGraph, setOttGraph] = useState<OttGraphResponse>({ ottLineGraph: [], ottRanking: [] });
    const [highlightedLine, setHighlightedLine] = useState<string | null>(null);

    useEffect(() => {
        fetchCard().then(setCard);
        fetchMonthlyDiaryGraph().then(setMonthlyDiary);
        fetchTypeDistribution().then(setTypeDist);
        fetchTypeGraph(selectedPeriod).then((data) => {
            console.log("typeGraph API 응답:", data);
            console.log("typeLineGraph:", data?.typeLineGraph);
            console.log("typeRanking:", data?.typeRanking);
            
            // API 응답이 비어있으면 임시 더미 데이터 사용
            if (!data || !data.typeLineGraph || data.typeLineGraph.length === 0) {
                console.log("API 데이터가 비어있어서 더미 데이터 사용");
                const dummyData = {
                    typeLineGraph: [
                        { axisLabel: "2025-01", type: "DRAMA", count: 5 },
                        { axisLabel: "2025-01", type: "MOVIE", count: 3 },
                        { axisLabel: "2025-02", type: "DRAMA", count: 7 },
                        { axisLabel: "2025-02", type: "MOVIE", count: 4 },
                        { axisLabel: "2025-03", type: "DRAMA", count: 6 },
                        { axisLabel: "2025-03", type: "MOVIE", count: 8 },
                    ],
                    typeRanking: [
                        { type: "DRAMA", totalCount: 18 },
                        { type: "MOVIE", totalCount: 15 },
                        { type: "BOOK", totalCount: 12 },
                        { type: "MUSIC", totalCount: 10 },
                    ]
                };
                setTypeGraph(dummyData);
            } else {
                setTypeGraph(data);
            }
        }).catch((error) => {
            console.error("API 호출 에러:", error);
            // 에러 발생 시에도 더미 데이터 사용
            const dummyData = {
                typeLineGraph: [
                    { axisLabel: "2025-01", type: "DRAMA", count: 5 },
                    { axisLabel: "2025-01", type: "MOVIE", count: 3 },
                    { axisLabel: "2025-02", type: "DRAMA", count: 7 },
                    { axisLabel: "2025-02", type: "MOVIE", count: 4 },
                    { axisLabel: "2025-03", type: "DRAMA", count: 6 },
                    { axisLabel: "2025-03", type: "MOVIE", count: 8 },
                ],
                typeRanking: [
                    { type: "DRAMA", totalCount: 18 },
                    { type: "MOVIE", totalCount: 15 },
                    { type: "BOOK", totalCount: 12 },
                    { type: "MUSIC", totalCount: 10 },
                ]
            };
            setTypeGraph(dummyData);
        });
        // 장르별 데이터 fetch
        fetchGenreGraph(selectedPeriod).then((data) => {
            if (!data || !data.genreLineGraph || data.genreLineGraph.length === 0) {
                // 더미 데이터
                const dummy = {
                  genreLineGraph: [
                    { axisLabel: "2025-01", genre: "드라마", count: 10 },
                    { axisLabel: "2025-01", genre: "액션", count: 5 },
                    { axisLabel: "2025-02", genre: "드라마", count: 12 },
                    { axisLabel: "2025-02", genre: "액션", count: 7 },
                  ],
                  genreRanking: [
                    { genre: "드라마", totalCount: 22 },
                    { genre: "액션", totalCount: 12 },
                  ]
                };
                setGenreGraph(dummy);
            } else {
                setGenreGraph(data);
            }
        }).catch(() => {
            // 더미 데이터
            const dummy = {
              genreLineGraph: [
                { axisLabel: "2025-01", genre: "드라마", count: 10 },
                { axisLabel: "2025-01", genre: "액션", count: 5 },
                { axisLabel: "2025-02", genre: "드라마", count: 12 },
                { axisLabel: "2025-02", genre: "액션", count: 7 },
              ],
              genreRanking: [
                { genre: "드라마", totalCount: 22 },
                { genre: "액션", totalCount: 12 },
              ]
            };
            setGenreGraph(dummy);
        });
        fetchEmotionGraph(selectedPeriod).then(setEmotionGraph);
        fetchOttGraph(selectedPeriod).then((data) => {
            if (!data || !data.ottLineGraph || data.ottLineGraph.length === 0) {
                // Dummy data for OTT
                const dummy = {
                    ottLineGraph: [
                        { axisLabel: "2025-01", ottName: "Netflix", count: 15 },
                        { axisLabel: "2025-01", ottName: "Tving", count: 10 },
                        { axisLabel: "2025-02", ottName: "Netflix", count: 18 },
                        { axisLabel: "2025-02", ottName: "Tving", count: 12 },
                    ],
                    ottRanking: [
                        { ottName: "Netflix", totalCount: 33 },
                        { ottName: "Tving", totalCount: 22 },
                        { ottName: "Watcha", totalCount: 15 },
                    ]
                };
                setOttGraph(dummy);
            } else {
                setOttGraph(data);
            }
        }).catch(() => {
            // Dummy data on error
            const dummy = {
                ottLineGraph: [
                    { axisLabel: "2025-01", ottName: "Netflix", count: 15 },
                    { axisLabel: "2025-01", ottName: "Tving", count: 10 },
                    { axisLabel: "2025-02", ottName: "Netflix", count: 18 },
                    { axisLabel: "2025-02", ottName: "Tving", count: 12 },
                ],
                ottRanking: [
                    { ottName: "Netflix", totalCount: 33 },
                    { ottName: "Tving", totalCount: 22 },
                    { ottName: "Watcha", totalCount: 15 },
                ]
            };
            setOttGraph(dummy);
        });
    }, [selectedPeriod]);

    const getTimeData = useMemo(() => {
        const chartData = convertTypeTimeCountsToChartData(typeGraph?.typeLineGraph ?? []);
        const ranking = typeGraph?.typeRanking || [];
        const isDaily = selectedPeriod === "최근한달" || selectedPeriod === "최근일주일";

        const allTypes = new Set<string>();
        chartData.forEach(d => Object.keys(d).forEach(k => k !== 'period' && allTypes.add(k)));
        ranking.forEach(r => allTypes.add(r.type));

        const dynamicTypeColors: Record<string, string> = { ...typeColors };
        let colorIndex = Object.keys(typeColors).length;
        allTypes.forEach(type => {
            if (!dynamicTypeColors[type]) {
                dynamicTypeColors[type] = `hsl(${colorIndex * 50}, 70%, 50%)`;
                colorIndex++;
            }
        });

        return {
            typeData: chartData,
            periodLabel: isDaily ? "일별" : "월별",
            rankings: ranking,
            colors: dynamicTypeColors,
        };
    }, [typeGraph, selectedPeriod]);

    const getGenreData = useMemo(() => {
        const chartData = convertGenreLineGraphToChartData(genreGraph.genreLineGraph ?? []);
        const ranking = genreGraph.genreRanking ?? [];
        
        const normalizeGenre = (g: string) => g.trim().toLowerCase();

        const allGenres = new Set<string>();
        chartData.forEach(d => Object.keys(d).forEach(k => k !== 'period' && allGenres.add(k)));
        ranking.forEach(r => allGenres.add(r.genre));

        const dynamicGenreColors: Record<string, string> = { ...genreColors };
        let colorIndex = Object.keys(genreColors).filter(k => !k.includes(":")).length / 2;

        allGenres.forEach(genre => {
            const normalized = normalizeGenre(genre);
            if (!dynamicGenreColors[normalized]) {
                const newColor = `hsl(${colorIndex * 60 + 180}, 70%, 50%)`;
                dynamicGenreColors[normalized] = newColor;
                colorIndex++;
            }
            dynamicGenreColors[genre] = dynamicGenreColors[normalized];
        });

        return { chartData, ranking, colors: dynamicGenreColors };
    }, [genreGraph]);

    const getEmotionData = useMemo(() => {
        const chartData = convertEmotionLineGraphToChartData(emotionGraph.emotionLineGraph ?? []);
        const ranking = emotionGraph.emotionRanking ?? [];

        // 모든 감정 키를 수집하고 동적 색상 맵 생성
        const allEmotions = new Set<string>();
        chartData.forEach(d => Object.keys(d).forEach(k => k !== 'period' && allEmotions.add(k)));
        ranking.forEach(r => allEmotions.add(r.emotion));

        const dynamicEmotionColors: Record<string, string> = { ...emotionColors };
        let colorIndex = Object.keys(emotionColors).length;
        allEmotions.forEach(emotion => {
            if (!dynamicEmotionColors[emotion]) {
                dynamicEmotionColors[emotion] = `hsl(${colorIndex * 60}, 70%, 50%)`;
                colorIndex++;
            }
        });

        return { chartData, ranking, colors: dynamicEmotionColors };
    }, [emotionGraph]);

    const getOttData = useMemo(() => {
      const chartData = convertOttLineGraphToChartData(ottGraph.ottLineGraph ?? []);
      const ranking = ottGraph.ottRanking ?? [];

      // 모든 OTT 키를 수집하고 동적 색상 맵 생성
      const allOtts = new Set<string>();
      chartData.forEach(d => Object.keys(d).forEach(k => k !== 'period' && allOtts.add(k)));
      ranking.forEach(r => allOtts.add(r.ottName));

      const dynamicOttColors: Record<string, string> = { ...ottColors };
      let colorIndex = Object.keys(ottColors).length;
      allOtts.forEach(ott => {
          if (!dynamicOttColors[ott]) {
              dynamicOttColors[ott] = `hsl(${colorIndex * 60}, 70%, 50%)`;
              colorIndex++;
          }
      });

      return { chartData, ranking, colors: dynamicOttColors };
    }, [ottGraph]);

const CustomTooltip = ({ active, payload, label, highlightedLine }: any) => {
  if (active && payload && payload.length && highlightedLine) {
    const data = payload.find((p: any) => p.dataKey === highlightedLine);
    if (!data) return null;

    return (
      <div className="bg-white p-2 border border-gray-200 rounded shadow-lg">
        <p className="font-bold">{data.name}</p>
        <p>{`${label} : ${data.value}`}</p>
      </div>
    );
  }

  return null;
};

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-6">
            <div className="max-w-7xl mx-auto space-y-6">
                {/* 헤더 */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold text-black flex items-center gap-2">
                            <BarChart3 className="h-8 w-8 text-blue-600" />
                            콘텐츠 감상 통계
                        </h1>
                        <p className="text-[#111] mt-1">나의 콘텐츠 감상 패턴을 분석해보세요</p>
                    </div>

                    {(activeTab === "type" || activeTab === "genre" || activeTab === "emotion" || activeTab === "ott") && (
                        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
                            <div className="flex items-center gap-2">
                                <Calendar className="h-5 w-5 text-gray-500" />
                                <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
                                    <SelectTrigger className="w-32 text-[#111]">
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="전체" className="text-[#111]">전체</SelectItem>
                                        <SelectItem value="이번년도" className="text-[#111]">이번년도</SelectItem>
                                        <SelectItem value="최근6개월" className="text-[#111]">최근 6개월</SelectItem>
                                        <SelectItem value="최근한달" className="text-[#111]">최근 한달</SelectItem>
                                        <SelectItem value="최근일주일" className="text-[#111]">최근 일주일</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>
                    )}
                </div>

                {/* 요약 카드 - API 데이터 사용 */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <Card className="bg-gradient-to-r from-blue-500 to-blue-600 text-white">
                        <CardHeader className="pb-2">
                            <CardTitle className="text-sm font-medium opacity-90">총 감상 수</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{card?.totalDiaryCount ?? 0}</div>
                            {/* <p className="text-xs opacity-75 mt-1">
                                <TrendingUp className="inline h-3 w-3 mr-1" />
                                전월 대비 +12%
                            </p> */}
                        </CardContent>
                    </Card>

                    <Card className="bg-gradient-to-r from-green-500 to-green-600 text-white">
                        <CardHeader className="pb-2">
                            <CardTitle className="text-sm font-medium opacity-90">평균 별점</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold flex items-center gap-1">
                                {card?.averageRating}
                                <Star className="h-5 w-5 fill-current" />
                            </div>
                            <p className="text-xs opacity-75 mt-1">5점 만점 기준</p>
                        </CardContent>
                    </Card>

                    <Card className="bg-gradient-to-r from-purple-500 to-purple-600 text-white">
                        <CardHeader className="pb-2">
                            <CardTitle className="text-sm font-medium opacity-90">선호 장르</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{card?.favoriteType}</div>
                            <p className="text-xs opacity-75 mt-1">{card?.favoriteTypeCount}회 감상</p>
                        </CardContent>
                    </Card>

                    <Card className="bg-gradient-to-r from-pink-500 to-pink-600 text-white">
                        <CardHeader className="pb-2">
                            <CardTitle className="text-sm font-medium opacity-90">주요 감정</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold flex items-center gap-1">
                                {card?.favoriteEmotion}
                                {/* <Heart className="h-5 w-5 fill-current" /> */}
                            </div>
                            <p className="text-xs opacity-75 mt-1">{card?.favoriteEmotionCount}회 경험</p>
                        </CardContent>
                    </Card>
                </div>

                {/* 탭 네비게이션 */}
                <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
                    <TabsList className="grid w-full grid-cols-5">
                        <TabsTrigger value="overview">개요</TabsTrigger>
                        <TabsTrigger value="type">타입별</TabsTrigger>
                        <TabsTrigger value="genre">장르별</TabsTrigger>
                        <TabsTrigger value="emotion">감정별</TabsTrigger>
                        <TabsTrigger value="ott">OTT별</TabsTrigger>
                    </TabsList>

                    <TabsContent value="overview" className="space-y-4">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <Card>
                                <CardHeader>
                                    <CardTitle>월별 감상 추이</CardTitle>
                                    <CardDescription>최근 6개월간 감상 패턴</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            <LineChart data={monthlyDiary.map(d => ({ period: d.period, 감상수: d.views }))} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="period" />
                                                <YAxis />
                                                <ChartTooltip />
                                                <Line
                                                    type="monotone"
                                                    dataKey="감상수"
                                                    stroke="#3b82f6"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#3b82f6" }}
                                                />
                                            </LineChart>
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader>
                                    <CardTitle>콘텐츠 타입 분포</CardTitle>
                                    <CardDescription>드라마, 영화, 책, 음악 비율</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            <PieChart>
                                                <Pie
                                                    data={typeDist}
                                                    cx="50%"
                                                    cy="50%"
                                                    outerRadius={80}
                                                    dataKey="count"
                                                    nameKey="type"
                                                    label={({ type, percent }) => `${type} ${(percent * 100).toFixed(0)}%`}
                                                >
                                                    {typeDist.map((entry, index) => (
                                                        <Cell key={`cell-${index}`} fill={`hsl(${index * 50}, 70%, 50%)`} />
                                                    ))}
                                                </Pie>
                                                <ChartTooltip />
                                            </PieChart>
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>

                    <TabsContent value="type" className="space-y-4">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            {/* 왼쪽: 차트 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>콘텐츠 타입별 {getTimeData.periodLabel} 추이</CardTitle>
                                    <CardDescription>드라마, 영화, 책, 음악별 {getTimeData.periodLabel} 감상 현황</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            {getTimeData.typeData.length === 0 ? (
                                                <div className="flex items-center justify-center h-full text-gray-400">
                                                    데이터가 없습니다.
                                                </div>
                                            ) : (
                                                <LineChart 
                                                    data={getTimeData.typeData} 
                                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <CartesianGrid strokeDasharray="3 3" />
                                                    <XAxis dataKey="period" />
                                                    <YAxis />
                                                    <Tooltip content={<CustomTooltip highlightedLine={highlightedLine} />} />
                                                    {getTimeData.typeData.length > 0 &&
                                                        Object.keys(getTimeData.typeData[0])
                                                            .filter((key) => key !== "period")
                                                            .map((type, idx) => (
                                                                <Line
                                                                    key={type}
                                                                    type="monotone"
                                                                    dataKey={type}
                                                                    stroke={getTimeData.colors[type]}
                                                                    strokeWidth={highlightedLine === type ? 4 : 2}
                                                                    strokeOpacity={highlightedLine && highlightedLine !== type ? 0.3 : 1}
                                                                    dot={{ r: 3 }}
                                                                    onMouseEnter={() => setHighlightedLine(type)}
                                                                />
                                                            ))
                                                    }
                                                </LineChart>
                                            )}
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>
                            {/* 오른쪽: 순위 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>타입별 순위</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-4">
                                        {(getTimeData.rankings ?? []).length === 0 ? (
                                            <div className="text-center text-gray-400 py-8">
                                                데이터가 없습니다.
                                            </div>
                                        ) : (
                                            (getTimeData.rankings ?? []).map((item, index) => (
                                                <div
                                                    key={item.type}
                                                    className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                                                    onMouseEnter={() => setHighlightedLine(item.type)}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <div
                                                        className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                        style={{ backgroundColor: getTimeData.colors[item.type] }}
                                                    >
                                                        {index + 1}
                                                    </div>
                                                    <div className="flex-1">
                                                        <div className="flex items-center justify-between">
                                                            <span className="font-medium">{item.type}</span>
                                                            <span className="text-sm text-gray-600">{item.totalCount}개</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                                                            <div
                                                                className="h-2 rounded-full"
                                                                style={{
                                                                    backgroundColor: getTimeData.colors[item.type],
                                                                    width: `${(item.totalCount / Math.max(...(getTimeData.rankings ?? []).map((d) => d.totalCount))) * 100}%`,
                                                                }}
                                                            />
                                                        </div>
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>

                    <TabsContent value="genre" className="space-y-4">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            {/* 왼쪽: 차트 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>장르별 {getTimeData.periodLabel} 추이</CardTitle>
                                    <CardDescription>주요 장르별 {getTimeData.periodLabel} 감상 현황</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer className="h-[350px] overflow-hidden">
                                        <ResponsiveContainer width="100%" height="100%">
                                            {getGenreData.chartData.length === 0 ? (
                                                <div className="flex items-center justify-center h-full text-gray-400">
                                                    데이터가 없습니다.
                                                </div>
                                            ) : (
                                                <LineChart 
                                                    data={getGenreData.chartData} 
                                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <CartesianGrid strokeDasharray="3 3" />
                                                    <XAxis dataKey="period" />
                                                    <YAxis />
                                                    <Tooltip content={<CustomTooltip highlightedLine={highlightedLine} />} />
                                                    {getGenreData.chartData.length > 0 &&
                                                        Object.keys(getGenreData.chartData[0])
                                                            .filter((key) => key !== "period")
                                                            .map((genre, idx) => (
                                                                <Line
                                                                    key={genre}
                                                                    type="monotone"
                                                                    dataKey={genre}
                                                                    stroke={getGenreData.colors[genre]}
                                                                    strokeWidth={highlightedLine === genre ? 4 : 2}
                                                                    strokeOpacity={highlightedLine && highlightedLine !== genre ? 0.3 : 1}
                                                                    dot={{ r: 3 }}
                                                                    onMouseEnter={() => setHighlightedLine(genre)}
                                                                />
                                                            ))
                                                    }
                                                </LineChart>
                                            )}
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>
                            {/* 오른쪽: 순위 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>장르별 순위</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-3 max-h-[350px] overflow-y-auto">
                                        {getGenreData.ranking.length === 0 ? (
                                            <div className="text-center text-gray-400 py-8">데이터가 없습니다.</div>
                                        ) : (
                                            getGenreData.ranking.map((item, index) => (
                                                <div
                                                    key={item.genre}
                                                    className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                                                    onMouseEnter={() => setHighlightedLine(item.genre)}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <div
                                                        className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                        style={{ backgroundColor: getGenreData.colors[item.genre] }}
                                                    >
                                                        {index + 1}
                                                    </div>
                                                    <div className="flex-1">
                                                        <div className="flex items-center justify-between">
                                                            <span className="font-medium">{item.genre}</span>
                                                            <span className="text-sm text-gray-600">{item.totalCount}개</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                                                            <div
                                                                className="h-2 rounded-full"
                                                                style={{
                                                                    backgroundColor: getGenreData.colors[item.genre],
                                                                    width: `${(item.totalCount / Math.max(...getGenreData.ranking.map((d) => d.totalCount))) * 100}%`,
                                                                }}
                                                            />
                                                        </div>
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>

                    <TabsContent value="emotion" className="space-y-4">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <Card>
                                <CardHeader>
                                    <CardTitle>감정별 {getTimeData.periodLabel} 추이</CardTitle>
                                    <CardDescription>감정별 {getTimeData.periodLabel} 경험 현황</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            {getEmotionData.chartData.length === 0 ? (
                                                <div className="flex items-center justify-center h-full text-gray-400">
                                                    데이터가 없습니다.
                                                </div>
                                            ) : (
                                                <LineChart 
                                                    data={getEmotionData.chartData} 
                                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <CartesianGrid strokeDasharray="3 3" />
                                                    <XAxis dataKey="period" />
                                                    <YAxis />
                                                    <Tooltip content={<CustomTooltip highlightedLine={highlightedLine} />} />
                                                    {Object.keys(getEmotionData.chartData[0] ?? {})
                                                        .filter((key) => key !== "period")
                                                        .map((emotion, idx) => (
                                                            <Line
                                                                key={emotion}
                                                                type="monotone"
                                                                dataKey={emotion}
                                                                stroke={getEmotionData.colors[emotion]}
                                                                strokeWidth={highlightedLine === emotion ? 4 : 2}
                                                                strokeOpacity={highlightedLine && highlightedLine !== emotion ? 0.3 : 1}
                                                                dot={{ r: 3 }}
                                                                onMouseEnter={() => setHighlightedLine(emotion)}
                                                            />
                                                        ))}
                                                </LineChart>
                                            )}
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader>
                                    <CardTitle>감정 순위</CardTitle>
                                    <CardDescription>가장 많이 경험한 감정들</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-4">
                                        {getEmotionData.ranking.length === 0 ? (
                                            <div className="text-center text-gray-400 py-8">데이터가 없습니다.</div>
                                        ) : (
                                            getEmotionData.ranking.map((item, index) => (
                                                <div
                                                    key={item.emotion}
                                                    className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                                                    onMouseEnter={() => setHighlightedLine(item.emotion)}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <div
                                                        className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                        style={{ backgroundColor: getEmotionData.colors[item.emotion] }}
                                                    >
                                                        {index + 1}
                                                    </div>
                                                    <div className="flex-1">
                                                        <div className="flex items-center justify-between">
                                                            <span className="font-medium">{item.emotion}</span>
                                                            <span className="text-sm text-gray-600">{item.totalCount}회</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                                                            <div
                                                                className="h-2 rounded-full"
                                                                style={{
                                                                    backgroundColor: getEmotionData.colors[item.emotion],
                                                                    width: `${(item.totalCount / Math.max(...getEmotionData.ranking.map((d) => d.totalCount))) * 100}%`,
                                                                }}
                                                            />
                                                        </div>
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>
                    
                    <TabsContent value="ott" className="space-y-4">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            {/* 왼쪽: 차트 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>OTT별 {getTimeData.periodLabel} 추이</CardTitle>
                                    <CardDescription>주요 OTT별 {getTimeData.periodLabel} 감상 현황</CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <ChartContainer className="h-[350px] overflow-hidden">
                                        <ResponsiveContainer width="100%" height="100%">
                                            {getOttData.chartData.length === 0 ? (
                                                <div className="flex items-center justify-center h-full text-gray-400">
                                                    데이터가 없습니다.
                                                </div>
                                            ) : (
                                                <LineChart 
                                                    data={getOttData.chartData} 
                                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <CartesianGrid strokeDasharray="3 3" />
                                                    <XAxis dataKey="period" />
                                                    <YAxis />
                                                    <Tooltip content={<CustomTooltip highlightedLine={highlightedLine} />} />
                                                    {getOttData.chartData.length > 0 &&
                                                        Object.keys(getOttData.chartData[0])
                                                            .filter((key) => key !== "period")
                                                            .map((ott, idx) => (
                                                                <Line
                                                                    key={ott}
                                                                    type="monotone"
                                                                    dataKey={ott}
                                                                    stroke={getOttData.colors[ott] || `hsl(${idx * 60}, 70%, 50%)`}
                                                                    strokeWidth={highlightedLine === ott ? 4 : 2}
                                                                    strokeOpacity={highlightedLine && highlightedLine !== ott ? 0.3 : 1}
                                                                    dot={{ r: 3 }}
                                                                    onMouseEnter={() => setHighlightedLine(ott)}
                                                                />
                                                            ))
                                                    }
                                                </LineChart>
                                            )}
                                        </ResponsiveContainer>
                                    </ChartContainer>
                                </CardContent>
                            </Card>
                            {/* 오른쪽: 순위 */}
                            <Card>
                                <CardHeader>
                                    <CardTitle>OTT별 순위</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-3 max-h-[350px] overflow-y-auto">
                                        {getOttData.ranking.length === 0 ? (
                                            <div className="text-center text-gray-400 py-8">데이터가 없습니다.</div>
                                        ) : (
                                            getOttData.ranking.map((item, index) => (
                                                <div
                                                    key={item.ottName}
                                                    className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                                                    onMouseEnter={() => setHighlightedLine(item.ottName)}
                                                    onMouseLeave={() => setHighlightedLine(null)}
                                                >
                                                    <div
                                                        className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                        style={{ backgroundColor: getOttData.colors[item.ottName] || `hsl(${index * 60}, 70%, 50%)` }}
                                                    >
                                                        {index + 1}
                                                    </div>
                                                    <div className="flex-1">
                                                        <div className="flex items-center justify-between">
                                                            <span className="font-medium">{item.ottName}</span>
                                                            <span className="text-sm text-gray-600">{item.totalCount}개</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                                                            <div
                                                                className="h-2 rounded-full"
                                                                style={{
                                                                    backgroundColor: getOttData.colors[item.ottName] || `hsl(${index * 60}, 70%, 50%)`,
                                                                    width: `${(item.totalCount / Math.max(...getOttData.ranking.map((d) => d.totalCount))) * 100}%`,
                                                                }}
                                                            />
                                                        </div>
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    )
}