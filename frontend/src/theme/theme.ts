import { createTheme } from "@mui/material/styles";

export const appTheme = createTheme({
  palette: {
    primary: {
      main: "#0b3d5c"
    },
    secondary: {
      main: "#d67a2a"
    },
    background: {
      default: "#f5f7fa"
    }
  },
  typography: {
    fontFamily: '"Segoe UI", "Helvetica Neue", Arial, sans-serif',
    h4: {
      fontWeight: 700
    },
    h5: {
      fontWeight: 700
    }
  },
  shape: {
    borderRadius: 12
  }
});
