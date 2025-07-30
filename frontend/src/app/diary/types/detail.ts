export type Diary = {
    title: string;
    rating: number;
    contentText: string;
    tagNames: string[];
};

export type DiaryInfoProps = {
    rating: number;
    contentText: string;
    tagNames: string[];
};

export type Comment = {
    id: number;
    nickname: string;
    profileImageUrl: string;
    content: string;
    createdAt: string;
}