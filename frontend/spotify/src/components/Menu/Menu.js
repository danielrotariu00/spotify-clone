import React from "react";
import { Menubar } from "primereact/menubar";
import useToken from "../../hooks/useToken";
import useUserId from "../../hooks/useUserId";
import useUserRoles from "../../hooks/useUserRoles";

export default function Menu() {
  const { token, setToken } = useToken();
  const { setUserId } = useUserId();
  const { userRoles, setUserRoles } = useUserRoles();

  const clientRole = "1";
  const artistRole = "2";
  const contentManagerRole = "3";
  const administratorRole = "4";

  const menuItems = [];

  const clientMenu = {
    label: "Client",
    items: [
      {
        label: "My Playlists",
        url: "/playlists",
      },
      {
        label: "Songs",
        url: "/songs",
      },
      {
        label: "Artists",
        url: "/artists",
      },
    ],
  };

  const artistMenu = {
    label: "Artist",
    items: [
      {
        label: "My Songs",
        url: "/artist-songs",
      },
    ],
  };

  const contentManagerMenu = {
    label: "Content Manager",
    items: [
      {
        label: "Songs",
        url: "/songs",
      },
      {
        label: "New Song",
        url: "/songs/new",
      },
      {
        label: "Artists",
        url: "/artists",
      },
      {
        label: "New Artist",
        url: "/artists/new",
      },
    ],
  };

  const administratorMenu = {
    label: "Administrator",
    items: [
      {
        label: "Users",
        url: "/users",
      },
    ],
  };

  const onLogoutClick = () => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "logoutRequest",

      data: {
        token: token,
      },

      success: function () {
        setToken({ token: null });
        setUserId({ userId: null });
        setUserRoles({ userRoles: null });
        window.location.reload(true);
      },
      error: function () {},
    });
  };

  const logoutMenu = {
    label: "Logout",
    command: () => onLogoutClick(),
  };

  if (userRoles.includes(clientRole)) {
    menuItems.push(clientMenu);
  }

  if (userRoles.includes(artistRole)) {
    menuItems.push(artistMenu);
  }

  if (userRoles.includes(contentManagerRole)) {
    menuItems.push(contentManagerMenu);
  }

  if (userRoles.includes(administratorRole)) {
    menuItems.push(administratorMenu);
  }

  menuItems.push(logoutMenu);

  return (
    <div>
      <div className="card">
        <Menubar model={menuItems} />
      </div>
    </div>
  );
}
