import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import AddIcon from "@material-ui/icons/Add";
import axios from "axios";
import { useRouteRefresh } from "../../utils";

export default function AddUser() {
  const [open, setOpen] = React.useState(false);
  const [imsi, setImsi] = React.useState("");
  const [msisdn, setMsisdn] = React.useState("");
  const [imei, setImei] = React.useState("");
  const [accountActive, setAccountActive] = React.useState("");
  const [location, setLocation] = React.useState("");
  const [sqn, setSqn] = React.useState(0);
  const [rand, setRand] = React.useState("");
  const refreshRoute = useRouteRefresh();

  const handleClickOpen = () => {
    setImsi("");
    setMsisdn("");
    setImei("");
    setAccountActive("");
    setLocation("");
    setSqn(0);
    setRand("");
    setOpen(true);
  };

  const handleClose = async () => {
    setOpen(false);
  };

  const handleAdd = async () => {
    try {
      if (imsi === "") {
        throw new Error("can't do no imsi");
      }
      await axios.post("/api/db/adduser", {
        imsi,
        msisdn,
        imei,
        active: accountActive,
        location,
        sqn,
        rand,
      });
      setOpen(false);
      refreshRoute();
    } catch (err) {
      console.log("err when addingUser is: " + err);
    }
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
                className="mb-5"
              />
              <TextField
                id="msisdn"
                label="msisdn:"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setMsisdn(e.target.value)
                }
                fullWidth
              />
              <TextField
                id="imei"
                label="imei:"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setImei(e.target.value)
                }
                fullWidth
              />
              <TextField
                id="active"
                select
                label="active:"
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                  setAccountActive(event.target.value);
                }}
                fullWidth
              >
                <MenuItem value={"0"}>Inactive</MenuItem>
                <MenuItem value={"1"}>Active</MenuItem>
              </TextField>
              <TextField
                id="location"
                label="location:"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setLocation(e.target.value)
                }
                fullWidth
              />
              <TextField
                label="sqn:"
                id="sqn"
                type="number"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setSqn(+e.target.value)
                }
                fullWidth
              />
              <TextField
                id="rand"
                label="rand:"
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
        }
      </Dialog>
    </div>
  );
}
