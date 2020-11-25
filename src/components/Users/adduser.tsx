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
  const [sqn, setSqn] = React.useState(666);
  const [rand, setRand] = React.useState("");
  const [sent, setSent] = React.useState(false);
  const handleClickOpen = () => {
    setImsi("");
    setMsisdn("");
    setImei("");
    setSqn(666);
    setRand("");
    setOpen(true);
    setSent(false);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleAdd = async () => {
    try {
      if (imsi === "") {
        throw new Error("can't do no imsi");
      }
      console.log("imsi is:");
      console.log(imsi);
      await axios.post("/api/db/adduser", {
        imsi,
        msisdn,
        imei,
        sqn,
        rand,
      });
    } catch (err) {
      console.log("err when addingUser is: " + err);
    }
    setSent(true);
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
              <DialogContentText>msisdn:</DialogContentText>
              <TextField
                id="msisdn"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setMsisdn(e.target.value)
                }
                fullWidth
              />
              <DialogContentText>imei:</DialogContentText>
              <TextField
                id="imei"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setImei(e.target.value)
                }
                fullWidth
              />
              <DialogContentText>sqn:</DialogContentText>
              <TextField
                id="sqn"
                type="number"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setSqn(+e.target.value)
                }
                fullWidth
              />
              <DialogContentText>rand:</DialogContentText>
              <TextField
                id="imei"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setRand(e.target.value)
                }
                fullWidth
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Cancel
              </Button>
              <Button onClick={handleAdd} color="primary">
                Add
              </Button>
            </DialogActions>
          </div>
        )}
      </Dialog>
    </div>
  );
}
