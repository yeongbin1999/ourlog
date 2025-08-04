"use client";

import { useEffect, useState, useRef } from "react";
import { AiOutlineMore } from "react-icons/ai";

export default function CommentMenuButton({
  onEdit,
  onDelete,
}: {
  onEdit: () => void;
  onDelete: () => void;
}) {
  const [open, setOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={menuRef}>
      <button
        className="text-gray-400 hover:text-gray-600 transition"
        onClick={() => setOpen((prev) => !prev)}
        aria-label="댓글 옵션"
      >
        <AiOutlineMore className="text-xl" />
      </button>

      {open && (
        <div className="absolute right-0 top-7 w-32 bg-white border border-gray-200 rounded-xl shadow-lg z-30 overflow-hidden">
          <button
            onClick={() => {
              setOpen(false);
              onEdit();
            }}
            className="w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 text-left transition"
          >
            수정
          </button>
          <button
            onClick={() => {
              setOpen(false);
              onDelete();
            }}
            className="w-full px-4 py-2 text-sm text-red-500 hover:bg-red-50 text-left transition"
          >
            삭제
          </button>
        </div>
      )}
    </div>
  );
}
