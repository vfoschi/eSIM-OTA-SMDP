import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import axios from "axios";

export default function AddUser() {
  const [open, setOpen] = React.useState(false);
  const [imsi, setImsi] = React.useState("");
  const [msisdn, setMsisdn] = React.useState("");
  const [imei, setImei] = React.useState("");
  const [sent, setSent] = React.useState(false);

  const handleClickOpen = () => {
    setImsi("");
    setMsisdn("");
    setImei("");
    setOpen(true);
    setSent(false);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleAdd = async () => {
    try {
      console.log('imsi is:');console.log(imsi);
      await axios.post("/api/db/adduser", {
        imsi,
        msisdn,
        imei,
      });
    } catch (err) {
      console.log("err when addingUser is: " + err);
    }
    setSent(true);
    setOpen(false);
  };

  return (
    <div>
      <IconButton onClick={handleClickOpen}>
        <AddIcon />
      </IconButton>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">Add a user</DialogTitle>
        {sent ? (
          "Sent!"
        ) : (
          <DialogContent>
            <DialogContentText>imsi:</DialogContentText>
            <TextField
              autoFocus
              id="imsi"
              onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                setImsi(e.target.value)
              }
              fullWidth
            />
            <DialogContentText>msisdn:</DialogContentText>
            <TextField
              autoFocus
              id="msisdn"
              onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                setMsisdn(e.target.value)
              }
              fullWidth
            />
            <DialogContentText>imei:</DialogContentText>
            <TextField
              autoFocus
              id="imei"
              onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                setImei(e.target.value)
              }
              fullWidth
            />
          </DialogContent>
        )}

        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleAdd} color="primary">
            Add
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
