"use client";

import { Row, Col } from "react-bootstrap";
import { useEffect, useState } from "react";
import TimelineCard from "../social/components/TimelineCard";
import { TimelineItem } from "../social/types/timeline";

export default function TimelinePage() {
  const [items, setItems] = useState<TimelineItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // timeline 데이터 가져오기
  useEffect(() => {
    fetch("/api/v1/timeline")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch timeline");
        return res.json();
      })
      .then((data: TimelineItem[]) => {
        setItems(data);
      })
      .catch((err) => {
        console.error(err);
        setError("타임라인을 불러오는 데 실패했습니다.");
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <main className="container mt-5">
      <h2 className="mb-4">🧾 타임라인 테스트</h2>

      {loading && <p>⏳ 불러오는 중...</p>}
      {error && <p className="text-danger">{error}</p>}

      {!loading && !error && (
        <Row className="g-4">
          {items.map((item) => (
            <Col key={item.id} xs={12} sm={6} md={4}>
              <TimelineCard item={item} />
            </Col>
          ))}
        </Row>
      )}
    </main>
  );
}
