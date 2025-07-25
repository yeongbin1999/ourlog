    "use client";

    import { Card, Button, Image, Row, Col } from "react-bootstrap";
    import { TimelineItem } from "../types/timeline";
    import { FaHeart, FaComment } from "react-icons/fa";

    interface Props {
      item: TimelineItem;
    }

    export default function TimelineCard({ item }: Props) {
      return (
        <Card className="shadow-sm rounded overflow-hidden mb-4" style={{ width: "100%", maxWidth: "360px" }}>
          {/* 이미지 */}
          {item.imageUrl && (
            <Card.Img
              variant="top"
              src={item.imageUrl}
              alt="Diary poster"
              style={{ height: "200px", objectFit: "cover" }}
            />
          )}

          {/* 카드 본문 */}
          <Card.Body>
            <Card.Title className="d-flex justify-content-between align-items-center">
              <span className="fw-bold">{item.title}</span>
            </Card.Title>
            <small className="text-muted">{new Date(item.createdAt).toLocaleDateString()}</small>
          </Card.Body>

          {/* 카드 하단 */}
          <Card.Footer className="bg-white border-0 pt-0">
            <Row className="align-items-center text-muted">
              <Col xs="auto" className="d-flex align-items-center gap-1">
                <FaHeart className="text-danger" />
                <span>{item.likeCount}</span>
              </Col>
              <Col xs="auto" className="d-flex align-items-center gap-1">
                <FaComment className="text-primary" />
                <span>{item.commentCount}</span>
              </Col>
              <Col className="d-flex align-items-center justify-content-end gap-2">
                {item.user.profileImageUrl && (
                  <Image
                    src={item.user.profileImageUrl}
                    roundedCircle
                    width={28}
                    height={28}
                    alt="profile"
                  />
                )}
                <strong className="text-dark">{item.user.nickname}</strong>
              </Col>
            </Row>
          </Card.Footer>
        </Card>
      );
    }
