import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/Delete";
import axios from "axios";

export default function DeleteUser() {
  const [open, setOpen] = React.useState(false);
  const [imsi, setImsi] = React.useState("");
  const [sent, setSent] = React.useState(false);

  const handleClickOpen = () => {
    setImsi("");
    setOpen(true);
    setSent(false);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleAdd = async () => {
    try {
      if (imsi === "") {
        throw new Error("can't do no input");
      }
      await axios.post("/api/db/deleteuser", {
        imsi,
      });
    } catch (err) {
      console.log("err when deleteUser is: " + err);
    }
    setSent(true);
  };

  return (
    <div>
      <IconButton onClick={handleClickOpen}>
        <DeleteIcon />
      </IconButton>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">Delete a user</DialogTitle>
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
            </DialogContent>

            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Cancel
              </Button>
              <Button onClick={handleAdd} color="primary">
                Delete
              </Button>
            </DialogActions>
          </div>
        )}
      </Dialog>
    </div>
  );
}
