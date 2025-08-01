export type Diary = {
    title: string;
    rating: number;
    contentText: string;
    tagNames: string[];
    genreNames: string[];
    ottNames: string[];
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

export type Content = {
    id: number;
    type: string;
    posterUrl: string;
    title: string;
    creatorName: string;
    description: string;
    releasedAt: string;
} 

export type ContentInfoProps = {
    content: Content;
    genreNames: string[];
    ottNames: string[];
}