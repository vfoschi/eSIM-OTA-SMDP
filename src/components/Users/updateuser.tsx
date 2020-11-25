import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import UpdateIcon from "@material-ui/icons/Update";
import axios from "axios";

export default function UpdateUser() {
  const [open, setOpen] = React.useState(false);
  const [imsi, setImsi] = React.useState("");
  const [imei, setImei] = React.useState("");
  const [sent, setSent] = React.useState(false);

  const handleClickOpen = () => {
    setImsi("");
    setImei("");
    setOpen(true);
    setSent(false);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleAdd = async () => {
    try {
      if (imsi === "" && imei === "") {
        throw new Error("can't do no input");
      }
      await axios.post("/api/db/updateuser", {
        imsi,
        imei,
      });
    } catch (err) {
      console.log("err when updateUser is: " + err);
    }
    setSent(true);
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
        <DialogTitle id="form-dialog-title">Update a user</DialogTitle>
        {sent ? (
          "Sent!"
        ) : (
          <div>
            <DialogContent>
              <DialogContentText>imsi:</DialogContentText>
              <TextField
                id="imsi"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setImsi(e.target.value)
                }
                fullWidth
              />
              <DialogContentText>imei:</DialogContentText>
              <TextField
                id="imsi"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setImei(e.target.value)
                }
                fullWidth
              />
            </DialogContent>

            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Cancel
              </Button>
              <Button onClick={handleAdd} color="primary">
                Update
              </Button>
            </DialogActions>
          </div>
        )}
      </Dialog>
    </div>
  );
}
