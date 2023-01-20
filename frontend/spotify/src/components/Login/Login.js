import React, { useState } from "react";
import "jquery.soap";

import PropTypes from "prop-types";
import "./Login.css";

export default function Login({ setToken, setUserId, setUserRoles }) {
  const [username, setUserName] = useState();
  const [password, setPassword] = useState();
  let token = null;

  const handleSubmit = (e) => {
    e.preventDefault();
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "loginRequest",

      data: {
        username: username,
        password: password,
      },

      success: function (SOAPResponse) {
        token =
          SOAPResponse.toXML().firstChild.lastChild.firstChild.firstChild
            .textContent;
        setToken({
          token,
        });

        $.soap({
          url: "http://localhost:8080/ws/",
          namespaceURL: "http://spotify.com/idm",
          namespaceQualifier: "gs",
          method: "authorizeRequest",

          data: {
            token: token,
          },

          success: function (SOAPResponse) {
            console.log(SOAPResponse.toXML());
            let childNodes =
              SOAPResponse.toXML().firstChild.lastChild.firstChild.childNodes;

            setUserId({
              userId: childNodes[0].textContent,
            });

            let roleIds = [];
            for (let i = 1; i < childNodes.length; i++) {
              roleIds.push(childNodes[i].textContent);
            }
            setUserRoles({
              userRoles: roleIds,
            });

            window.location.reload(true);
          },
          error: function (SOAPResponse) {},
        });
      },
      error: function (SOAPResponse) {},
    });
  };

  return (
    <div className="login-wrapper">
      <h1>Please Log In</h1>
      <form onSubmit={handleSubmit}>
        <label>
          <p>Username</p>
          <input type="text" onChange={(e) => setUserName(e.target.value)} />
        </label>
        <label>
          <p>Password</p>
          <input
            type="password"
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <div>
          <button type="submit">Submit</button>
        </div>
      </form>
    </div>
  );
}

Login.propTypes = {
  setToken: PropTypes.func.isRequired,
  setUserId: PropTypes.func.isRequired,
  setUserRoles: PropTypes.func.isRequired,
};
