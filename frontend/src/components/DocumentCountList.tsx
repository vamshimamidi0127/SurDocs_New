import { List, ListItem, ListItemText, Paper, Typography } from "@mui/material";
import type { DocumentCountItem } from "../types/api";

type DocumentCountListProps = {
  items: DocumentCountItem[];
};

export function DocumentCountList({ items }: DocumentCountListProps) {
  if (items.length === 0) {
    return <Typography color="text.secondary">No document counts returned.</Typography>;
  }

  return (
    <Paper variant="outlined">
      <List disablePadding>
        {items.map((item) => (
          <ListItem key={item.type} divider>
            <ListItemText primary={item.type} secondary={`Count: ${item.count}`} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}
