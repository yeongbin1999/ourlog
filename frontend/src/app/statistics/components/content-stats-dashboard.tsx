"use client"

import { useState, useMemo, useEffect } from "react"
import { Calendar, BarChart3, Heart, Star, TrendingUp, CalendarIcon } from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ChartContainer, ChartTooltip } from "@/components/ui/chart"
import { Line, LineChart, Pie, PieChart, Cell, ResponsiveContainer, XAxis, YAxis, CartesianGrid } from "recharts"
import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar as CalendarComponent } from "@/components/ui/calendar"
import { format } from "date-fns"
import { ko } from "date-fns/locale"


const BASE_URL = "http://localhost:8080/api/v1/statistics";

async function fetchCard() {
    const res = await fetch(`${BASE_URL}/card`);
    return res.json();
}
async function fetchMonthlyDiaryGraph() {
    const res = await fetch(`${BASE_URL}/monthly-diary-graph`);
    return res.json();
}
async function fetchTypeDistribution() {
    const res = await fetch(`${BASE_URL}/type-distribution`);
    return res.json();
}
async function fetchTypeGraph(userId: number, period: string) {
  // period를 PeriodOption enum 값으로 변환
  const periodMap: Record<string, string> = {
    "전체": "ALL",
    "이번년도": "THIS_YEAR", 
    "최근6개월": "LAST_6_MONTHS",
    "최근한달": "LAST_MONTH",
    "최근일주일": "LAST_WEEK"
  };
  
  const periodOption = periodMap[period] || "ALL";
  
  const res = await fetch(`${BASE_URL}/type-graph?userId=${userId}&period=${periodOption}`);
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

// 월별 데이터 (기본 데이터)
const monthlyTypeData = [
    { period: "1월", 드라마: 10, 영화: 8, 책: 6, 음악: 12 },
    { period: "2월", 드라마: 12, 영화: 9, 책: 7, 음악: 11 },
    { period: "3월", 드라마: 11, 영화: 10, 책: 8, 음악: 13 },
    { period: "4월", 드라마: 9, 영화: 11, 책: 9, 음악: 10 },
    { period: "5월", 드라마: 8, 영화: 12, 책: 10, 음악: 9 },
    { period: "6월", 드라마: 7, 영화: 13, 책: 11, 음악: 8 },
]

const monthlyGenreData = [
    { period: "1월", 드라마: 15, 액션: 10, 로맨스: 8, 공포: 7, 코미디: 6 },
    { period: "2월", 드라마: 14, 액션: 12, 로맨스: 9, 공포: 6, 코미디: 7 },
    { period: "3월", 드라마: 13, 액션: 11, 로맨스: 10, 공포: 5, 코미디: 8 },
    { period: "4월", 드라마: 12, 액션: 9, 로맨스: 11, 공포: 4, 코미디: 9 },
    { period: "5월", 드라마: 11, 액션: 8, 로맨스: 12, 공포: 3, 코미디: 10 },
    { period: "6월", 드라마: 10, 액션: 7, 로맨스: 13, 공포: 2, 코미디: 11 },
]

const monthlyEmotionData = [
    { period: "1월", 감동: 12, 재미: 10, 긴장: 8, 슬픔: 7, 분노: 6, 공포: 5 },
    { period: "2월", 감동: 11, 재미: 12, 긴장: 9, 슬픔: 6, 분노: 7, 공포: 4 },
    { period: "3월", 감동: 10, 재미: 11, 긴장: 10, 슬픔: 5, 분노: 8, 공포: 3 },
    { period: "4월", 감동: 9, 재미: 9, 긴장: 11, 슬픔: 4, 분노: 9, 공포: 2 },
    { period: "5월", 감동: 8, 재미: 8, 긴장: 12, 슬픔: 3, 분노: 10, 공포: 1 },
    { period: "6월", 감동: 7, 재미: 7, 긴장: 13, 슬픔: 2, 분노: 11, 공포: 0 },
]

// 타입별 색상 맵 추가
const typeColors: Record<string, string> = {
  "DRAMA": "#8884d8",
  "MOVIE": "#82ca9d", 
  "BOOK": "#ffc658",
  "MUSIC": "#ff7c7c"
};

// 변환 함수 추가
function convertTypeTimeCountsToChartData(timeCounts: TypeLineGraphDto[]): Record<string, any>[] {
  // { period: { 드라마: 10, 영화: 8, ... } } 형태로 그룹핑
  const periodMap: Record<string, Record<string, any>> = {};
  timeCounts.forEach(({ axisLabel, type, count }) => {
    if (!periodMap[axisLabel]) periodMap[axisLabel] = { period: axisLabel };
    periodMap[axisLabel][type] = count;
  });
  // 배열로 변환
  return Object.values(periodMap);
}

// 커스텀 툴팁 컴포넌트
const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
        return (
            <div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg max-w-xs z-50">
                <p className="font-semibold text-gray-800 mb-2">{label}</p>
                {payload.map((entry: any, index: number) => (
                    <p key={index} className="text-sm" style={{ color: entry.color }}>
                        {entry.dataKey}: <span className="font-bold">{entry.value}개</span>
                    </p>
                ))}
            </div>
        )
    }
    return null
}

export default function Component() {
    const [selectedPeriod, setSelectedPeriod] = useState("전체")
    const [startDate, setStartDate] = useState<Date>()
    const [endDate, setEndDate] = useState<Date>()
    const [activeTab, setActiveTab] = useState("overview")
    const [card, setCard] = useState<any>(null);
    const [monthlyDiary, setMonthlyDiary] = useState<any[]>([]);
    const [typeDist, setTypeDist] = useState<any[]>([]);
    const [typeGraph, setTypeGraph] = useState<TypeGraphResponse>({ typeLineGraph: [], typeRanking: [] });

    useEffect(() => {
        fetchCard().then(setCard);
        fetchMonthlyDiaryGraph().then(setMonthlyDiary);
        fetchTypeDistribution().then(setTypeDist);
        fetchTypeGraph(1, selectedPeriod).then((data) => {
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
    }, [selectedPeriod]);

    // 기간에 따른 데이터 선택
    const getTimeData = useMemo(() => {
        const isDaily = selectedPeriod === "최근한달" || selectedPeriod === "최근일주일"
        const currentRankings = typeGraph?.typeRanking || [] // API 데이터 사용

        const convertedData = convertTypeTimeCountsToChartData(typeGraph?.typeLineGraph ?? []);
        console.log("변환된 차트 데이터:", convertedData);
        console.log("순위 데이터:", currentRankings);

        if (isDaily) {
            return {
                typeData: convertedData, // 일별도 변환 함수 사용
                genreData: monthlyGenreData,
                emotionData: monthlyEmotionData,
                periodLabel: "일별",
                rankings: currentRankings,
            }
        } else {
            return {
                typeData: convertedData,
                genreData: monthlyGenreData,
                emotionData: monthlyEmotionData,
                periodLabel: "월별",
                rankings: currentRankings,
            }
        }
    }, [selectedPeriod, typeGraph])

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

                    {(activeTab === "type" || activeTab === "genre" || activeTab === "emotion") && (
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
                                        <SelectItem value="사용자지정" className="text-[#111]">사용자 지정</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            {selectedPeriod === "사용자지정" && (
                                <div className="flex items-center gap-2">
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant="outline"
                                                className="w-[140px] justify-start text-left font-normal bg-transparent text-[#111]"
                                            >
                                                <CalendarIcon className="mr-2 h-4 w-4" />
                                                {startDate ? format(startDate, "yyyy-MM-dd", { locale: ko }) : "시작일"}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0" align="start">
                                            <CalendarComponent mode="single" selected={startDate} onSelect={setStartDate} />
                                        </PopoverContent>
                                    </Popover>

                                    <span className="text-gray-500">~</span>

                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant="outline"
                                                className="w-[140px] justify-start text-left font-normal bg-transparent text-[#111]"
                                            >
                                                <CalendarIcon className="mr-2 h-4 w-4" />
                                                {endDate ? format(endDate, "yyyy-MM-dd", { locale: ko }) : "종료일"}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0" align="start">
                                            <CalendarComponent mode="single" selected={endDate} onSelect={setEndDate} />
                                        </PopoverContent>
                                    </Popover>
                                </div>
                            )}
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
                    <TabsList className="grid w-full grid-cols-4">
                        <TabsTrigger value="overview">개요</TabsTrigger>
                        <TabsTrigger value="type">타입별</TabsTrigger>
                        <TabsTrigger value="genre">장르별</TabsTrigger>
                        <TabsTrigger value="emotion">감정별</TabsTrigger>
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
                                        config={{
                                            views: {
                                                label: "감상 수",
                                                color: "#3b82f6",
                                            },
                                        }}
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            <LineChart data={monthlyDiary.map(d => ({ period: d.period, 감상수: d.views }))} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="period" />
                                                <YAxis />
                                                <ChartTooltip content={<CustomTooltip />} />
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
                                        config={{
                                            count: {
                                                label: "감상 수",
                                                color: "#10b981",
                                            },
                                        }}
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
                                                <ChartTooltip content={<CustomTooltip />} />
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
                                        config={{
                                            드라마: { label: "드라마", color: "#8884d8" },
                                            영화: { label: "영화", color: "#82ca9d" },
                                            책: { label: "책", color: "#ffc658" },
                                            음악: { label: "음악", color: "#ff7c7c" },
                                        }}
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            {getTimeData.typeData.length === 0 ? (
                                                <div className="flex items-center justify-center h-full text-gray-400">
                                                    데이터가 없습니다.
                                                </div>
                                            ) : (
                                                <LineChart data={getTimeData.typeData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                    <CartesianGrid strokeDasharray="3 3" />
                                                    <XAxis dataKey="period" />
                                                    <YAxis />
                                                    <ChartTooltip content={<CustomTooltip />} />
                                                    {getTimeData.typeData.length > 0 &&
                                                        Object.keys(getTimeData.typeData[0])
                                                            .filter((key) => key !== "period")
                                                            .map((type, idx) => (
                                                                <Line
                                                                    key={type}
                                                                    type="monotone"
                                                                    dataKey={type}
                                                                    stroke={typeColors[type] || `hsl(${idx * 50}, 70%, 50%)`}
                                                                    strokeWidth={2}
                                                                    dot={{ fill: typeColors[type] || `hsl(${idx * 50}, 70%, 50%)`, r: 4 }}
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
                                        {(typeGraph?.typeRanking ?? []).length === 0 ? (
                                            <div className="text-center text-gray-400 py-8">
                                                데이터가 없습니다.
                                            </div>
                                        ) : (
                                            (typeGraph?.typeRanking ?? []).map((item, index) => (
                                                <div key={item.type} className="flex items-center gap-3">
                                                    <div
                                                        className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                        style={{ backgroundColor: typeColors[item.type] || `hsl(${index * 50}, 70%, 50%)` }}
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
                                                                    backgroundColor: typeColors[item.type] || `hsl(${index * 50}, 70%, 50%)`,
                                                                    width: `${(item.totalCount / Math.max(...(typeGraph?.typeRanking ?? []).map((d) => d.totalCount))) * 100}%`,
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
                                    <ChartContainer
                                        config={{
                                            드라마: { label: "드라마", color: "#8884d8" },
                                            액션: { label: "액션", color: "#82ca9d" },
                                            로맨스: { label: "로맨스", color: "#ffc658" },
                                            공포: { label: "공포", color: "#ff7c7c" },
                                            코미디: { label: "코미디", color: "#8dd1e1" },
                                        }}
                                        className="h-[350px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            <LineChart data={monthlyGenreData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="period" />
                                                <YAxis />
                                                <ChartTooltip content={<CustomTooltip />} />

                                                <YAxis />
                                                <ChartTooltip content={<CustomTooltip />} />
                                                <Line
                                                    type="monotone"
                                                    dataKey="드라마"
                                                    stroke="#8884d8"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#8884d8", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="액션"
                                                    stroke="#82ca9d"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#82ca9d", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="로맨스"
                                                    stroke="#ffc658"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#ffc658", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="공포"
                                                    stroke="#ff7c7c"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#ff7c7c", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="코미디"
                                                    stroke="#8dd1e1"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#8dd1e1", r: 3 }}
                                                />
                                            </LineChart>
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
                                        <h3 className="font-semibold text-lg mb-3">장르별 순위</h3>
                                        {(typeGraph?.typeRanking ?? []).map((item, index) => (
                                            <div key={item.type} className="flex items-center gap-3">
                                                <div
                                                    className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                    style={{ backgroundColor: `hsl(${index * 50}, 70%, 50%)` }}
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
                                                                backgroundColor: `hsl(${index * 50}, 70%, 50%)`,
                                                                width: `${(item.totalCount / Math.max(...(typeGraph?.typeRanking ?? []).map((d) => d.totalCount))) * 100}%`,
                                                            }}
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
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
                                        config={{
                                            감동: { label: "감동", color: "#ff6b6b" },
                                            재미: { label: "재미", color: "#4ecdc4" },
                                            긴장: { label: "긴장", color: "#45b7d1" },
                                            슬픔: { label: "슬픔", color: "#96ceb4" },
                                            분노: { label: "분노", color: "#feca57" },
                                            공포: { label: "공포", color: "#ff9ff3" },
                                        }}
                                        className="h-[300px] overflow-hidden"
                                    >
                                        <ResponsiveContainer width="100%" height="100%">
                                            <LineChart data={monthlyEmotionData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="period" />
                                                <YAxis />
                                                <ChartTooltip content={<CustomTooltip />} />
                                                <Line
                                                    type="monotone"
                                                    dataKey="감동"
                                                    stroke="#ff6b6b"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#ff6b6b", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="재미"
                                                    stroke="#4ecdc4"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#4ecdc4", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="긴장"
                                                    stroke="#45b7d1"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#45b7d1", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="슬픔"
                                                    stroke="#96ceb4"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#96ceb4", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="분노"
                                                    stroke="#feca57"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#feca57", r: 3 }}
                                                />
                                                <Line
                                                    type="monotone"
                                                    dataKey="공포"
                                                    stroke="#ff9ff3"
                                                    strokeWidth={2}
                                                    dot={{ fill: "#ff9ff3", r: 3 }}
                                                />
                                            </LineChart>
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
                                        {(typeGraph?.typeRanking ?? []).map((item, index) => (
                                            <div key={item.type} className="flex items-center gap-3">
                                                <div
                                                    className="flex items-center justify-center w-6 h-6 rounded-full text-white text-xs font-semibold"
                                                    style={{ backgroundColor: `hsl(${index * 50}, 70%, 50%)` }}
                                                >
                                                    {index + 1}
                                                </div>
                                                <div className="flex-1">
                                                    <div className="flex items-center justify-between">
                                                        <span className="font-medium">{item.type}</span>
                                                        <span className="text-sm text-gray-600">{item.totalCount}회</span>
                                                    </div>
                                                    <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                                                        <div
                                                            className="h-2 rounded-full"
                                                            style={{
                                                                backgroundColor: `hsl(${index * 50}, 70%, 50%)`,
                                                                width: `${(item.totalCount / Math.max(...(typeGraph?.typeRanking ?? []).map((d) => d.totalCount))) * 100}%`,
                                                            }}
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
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
