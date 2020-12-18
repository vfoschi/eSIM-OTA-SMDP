import React from "react";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import { DataGrid, RowsProp, ColDef } from "@material-ui/data-grid";
import { useGetTableAPI, useGetColumnsAPI } from "../../utils";
export default function PDNs() {
  const [pdns, isLoading, isError] = useGetTableAPI(`api`, "pdn");
  const [cols, isColsLoading, isColsError] = useGetColumnsAPI(`api`, "pdn");
  if (isColsLoading || isLoading) {
    return <CircularProgress />;
  }
  if (isColsError || isError) {
    return (
      <div>
        An error happened. Checked logs. <br />{" "}
      </div>
    );
  }
  const columns: ColDef[] = cols.map((col, index) => ({
    field: "col" + index,
    headerName: col,
    width: 200,
  }));

  const rows: RowsProp = pdns.map((pdn, index) => {
    let rowObj: { [colName: string]: string; id: string } = { id: index + "" };
    for (let colIndex in cols) {
      const colName = "col" + colIndex;
      rowObj[colName] = pdn[colIndex];
    }
    return rowObj;
  });
  return (
    <div style={{ height: 800, width: "100%" }}>
      <Typography variant="body1">
        All PDNs: <br />
      </Typography>
      <DataGrid rows={rows} columns={columns} />
    </div>
  );
}
