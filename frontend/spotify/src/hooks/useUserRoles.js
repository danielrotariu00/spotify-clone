import { useState } from "react";

export default function useUserRoles() {
  const getUserRoles = () => {
    const userRolesString = localStorage.getItem("userRoles");
    const userRoles = JSON.parse(userRolesString);
    return userRoles?.userRoles;
  };

  const [userRoles, setUserRoles] = useState(getUserRoles());

  const saveUserRoles = (userRoles) => {
    localStorage.setItem("userRoles", JSON.stringify(userRoles));
    setUserRoles(userRoles.userRoles);
  };

  return {
    setUserRoles: saveUserRoles,
    userRoles,
  };
}
