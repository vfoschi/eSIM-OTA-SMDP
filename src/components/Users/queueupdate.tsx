import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import UpdateIcon from "@material-ui/icons/Update";
import MenuItem from "@material-ui/core/MenuItem";
import axios from "axios";
import { useRouteRefresh } from "../../utils";

export default function QueueUpdate() {
  const [open, setOpen] = React.useState(false);
  const [imsi, setImsi] = React.useState("");
  const [access_restriction, setAccess_Restriction] = React.useState("");
  const [accountActive, setAccountActive] = React.useState("");
  const refreshRoute = useRouteRefresh();

  const handleClickOpen = () => {
    setImsi("");
    setAccountActive("");
    setAccess_Restriction("");
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleQueue = async () => {
    try {
      if (imsi === "") {
        throw new Error("can't do no input");
      }
      await axios.post("/api/db/updatequeue", {
        imsi,
        active: accountActive,
        access_restriction,
      });
    } catch (err) {
      console.log("err when updateUser is: " + err);
    }
    setOpen(false);
    refreshRoute();
  };

  return (
    <div>
      <IconButton onClick={handleClickOpen}>
        <UpdateIcon />
      </IconButton>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">
          Queue an update for a user
        </DialogTitle>
        {
          <div>
            <DialogContent>
              <TextField
                id="imsi"
                label="imsi:"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setImsi(e.target.value)
                }
                fullWidth
              />
              <TextField
                id="active"
                select
                label="active:"
                value={accountActive}
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                  setAccountActive(event.target.value);
                }}
                fullWidth
              >
                <MenuItem value={"0"}>Inactive</MenuItem>
                <MenuItem value={"1"}>Active</MenuItem>
              </TextField>
              <TextField
                label="access_restriction:"
                id="access_restriction"
                type="number"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setAccess_Restriction(e.target.value)
                }
                fullWidth
              />
            </DialogContent>

            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Cancel
              </Button>
              <Button onClick={handleQueue} color="primary">
                Queue
              </Button>
            </DialogActions>
          </div>
        }
      </Dialog>
    </div>
  );
}
