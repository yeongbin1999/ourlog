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
        className="text-gray-500 hover:text-gray-700"
        onClick={() => setOpen((prev) => !prev)}
      >
        <AiOutlineMore className="text-2xl" />
      </button>
      {open && (
        <div className="absolute right-0 mt-2 w-24 bg-white border rounded shadow-lg z-10">
          <button
            onClick={() => {
              setOpen(false);
              onEdit();
            }}
            className="block w-full px-3 py-2 text-sm hover:bg-gray-100 text-left"
          >
            수정
          </button>
          <button
            onClick={() => {
              setOpen(false);
              onDelete();
            }}
            className="block w-full px-3 py-2 text-sm hover:bg-gray-100 text-left text-red-500"
          >
            삭제
          </button>
        </div>
      )}
    </div>
  );
}
