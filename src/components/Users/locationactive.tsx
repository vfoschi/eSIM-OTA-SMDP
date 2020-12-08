import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import { IconButton } from "@material-ui/core";
import LocationSearchingIcon from "@material-ui/icons/LocationSearching";
import MenuItem from "@material-ui/core/MenuItem";
import axios from "axios";
import { useRouteRefresh } from "../../utils";

export default function LocationActive() {
  const [open, setOpen] = React.useState(false);
  const [accountActive, setAccountActive] = React.useState("");
  const [location, setLocation] = React.useState("");
  const refreshRoute = useRouteRefresh();

  const handleClickOpen = () => {
    setAccountActive("");
    setLocation("");
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleQueue = async () => {
    try {
      await axios.post("/api/db/locationactive", {
        location,
        active: accountActive,
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
        <LocationSearchingIcon />
      </IconButton>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="form-dialog-title"
      >
        <DialogTitle id="form-dialog-title">
          Activate/Deactivate a Region
        </DialogTitle>
        {
          <div>
            <DialogContent>
              <TextField
                label="location:"
                id="location"
                onChange={async (e: React.ChangeEvent<HTMLInputElement>) =>
                  setLocation(e.target.value)
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
