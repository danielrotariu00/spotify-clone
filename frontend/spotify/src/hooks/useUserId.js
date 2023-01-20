import { useState } from "react";

export default function useUserId() {
  const getUserId = () => {
    const userIdString = localStorage.getItem("userId");
    const userId = JSON.parse(userIdString);
    return userId?.userId;
  };

  const [userId, setUserId] = useState(getUserId());

  const saveUserId = (userId) => {
    localStorage.setItem("userId", JSON.stringify(userId));
    setUserId(userId.userId);
  };

  return {
    setUserId: saveUserId,
    userId,
  };
}
