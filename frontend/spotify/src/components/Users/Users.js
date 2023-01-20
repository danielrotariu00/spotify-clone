import React, { useState, useEffect } from "react";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { ListBox } from "primereact/listbox";
import { Dialog } from "primereact/dialog";
import useToken from "../../hooks/useToken";
import useUserRoles from "../../hooks/useUserRoles";

export default function Users() {
  const { token } = useToken();
  const { userRoles } = useUserRoles();
  const administratorRole = "4";

  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [displayRoles, setDisplayRoles] = useState(false);

  useEffect(() => {
    getUsers();
    getRoles();
  }, []);

  const getUsers = () => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "getUsersRequest",

      data: {
        token: token,
      },

      success: function (SOAPResponse) {
        let usersNodes =
          SOAPResponse.toXML().firstChild.lastChild.firstChild.childNodes;

        let users = [];
        for (let i = 0; i < usersNodes.length; i++) {
          let userNode = usersNodes[i].childNodes;

          let id = userNode[0].textContent;
          let username = userNode[1].textContent;

          let roles = [];
          for (let j = 2; j < userNode.length; j++) {
            let roleNode = userNode[j].childNodes;
            let id = roleNode[0].textContent;
            let name = roleNode[1].textContent;

            roles.push({
              id: id,
              name: name,
            });
          }

          users.push({
            id: id,
            username: username,
            roles: roles,
          });
        }
        setUsers(users);
      },
      error: function () {},
    });
  };

  const getRoles = () => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "getRolesRequest",

      data: {
        token: token,
      },

      success: function (SOAPResponse) {
        let rolesNodes =
          SOAPResponse.toXML().firstChild.lastChild.firstChild.childNodes;

        let roles = [];
        for (let i = 0; i < rolesNodes.length; i++) {
          let roleNode = rolesNodes[i].childNodes;

          let id = roleNode[0].textContent;
          let name = roleNode[1].textContent;

          roles.push({
            id: id,
            name: name,
          });
        }
        setRoles(roles);
      },
      error: function () {},
    });
  };

  const onDeleteUserClick = (userId) => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "deleteUserRequest",

      data: {
        token: token,
        userId: userId,
      },

      success: function () {
        getUsers();
      },
      error: function () {},
    });
  };

  const onDeleteRoleClick = (userId, roleId) => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "deleteRoleRequest",

      data: {
        token: token,
        userId: userId,
        roleId: roleId,
      },

      success: function () {
        window.location.reload(true);
      },
      error: function () {},
    });
  };
  const onConfirmClick = () => {
    let $ = require("jquery");
    require("jquery.soap");

    $.soap({
      url: "http://localhost:8080/ws/",
      namespaceURL: "http://spotify.com/idm",
      namespaceQualifier: "gs",
      method: "addRoleRequest",

      data: {
        token: token,
        userId: selectedUser.id,
        roleId: selectedRole.id,
      },

      success: function () {
        window.location.reload(true);
      },
      error: function () {},
    });
  };

  const onAddClick = () => {
    setDisplayRoles(true);
  };

  const onHide = () => {
    setDisplayRoles(false);
  };

  const renderFooter = () => {
    return (
      <div>
        <Button
          label="Cancel"
          icon="pi pi-times"
          onClick={() => onHide()}
          className="p-button-text"
        />
        <Button
          label="Add"
          icon="pi pi-check"
          onClick={() => {
            onConfirmClick();
            onHide();
          }}
          autoFocus
          disabled={selectedRole == null}
        />
      </div>
    );
  };

  if (!userRoles.includes(administratorRole)) {
    return <h2>You are not authorized to view this page</h2>;
  }

  return (
    <div>
      <Splitter
        style={{
          height: "93.5vh",
          backgroundColor: "black",
        }}
        layout="horizontal"
      >
        <SplitterPanel
          style={{ overflowY: "scroll" }}
          className="flex align-items-center justify-content-center"
        >
          <h2>User List</h2>
          <ul>
            {users.map((user) => (
              <div>
                <Card
                  title={user.username}
                  style={{ height: "8em", width: "20em" }}
                >
                  <div>
                    <Button
                      label="View Details"
                      onClick={() => {
                        setSelectedUser(user);
                      }}
                    />
                    &emsp;
                    <Button
                      label="Delete"
                      onClick={() => {
                        onDeleteUserClick(user.id);
                      }}
                    />
                  </div>
                </Card>
                <br />
              </div>
            ))}
          </ul>
        </SplitterPanel>
        <SplitterPanel className="flex align-items-center justify-content-center">
          <h2>User Details</h2>
          <h3>Id: {selectedUser?.id}</h3>
          <h3>Username: {selectedUser?.username}</h3>
          <h3>Roles:</h3>
          <ul>
            {selectedUser?.roles.map((role) => (
              <div>
                <Card
                  title={
                    <div>
                      {role.id}: {role.name}
                    </div>
                  }
                  style={{ height: "8em", width: "20em" }}
                >
                  <div>
                    <Button
                      label="Delete"
                      onClick={() => {
                        onDeleteRoleClick(selectedUser.id, role.id);
                      }}
                    />
                  </div>
                </Card>
                <br />
              </div>
            ))}
          </ul>
          <Button label="Add Role" onClick={onAddClick} />
          <Dialog
            header="Select Role"
            visible={displayRoles}
            style={{ width: "50vw" }}
            footer={renderFooter()}
            onHide={() => onHide()}
          >
            <ListBox
              value={selectedRole}
              options={roles}
              onChange={(e) => setSelectedRole(e.value)}
              optionLabel="name"
              style={{ width: "15rem" }}
            />
          </Dialog>
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
