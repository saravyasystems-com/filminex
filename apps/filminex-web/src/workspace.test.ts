import { describe, expect, it } from "vitest";
import { canEdit, navigation, visibleNavigation } from "./workspace";

describe("workspace presentation", () => {
  it("hides capabilities the workspace does not use", () => {
    const visible = visibleNavigation(navigation, new Set(["core"]));

    expect(visible.map((item) => item.label)).toEqual([
      "Story",
      "Characters",
      "Locations"
    ]);
  });

  it("reveals optional tools only when entitled", () => {
    const visible = visibleNavigation(
      navigation,
      new Set(["core", "ai", "animation"])
    );

    expect(visible.map((item) => item.label)).toContain("AI Studio");
    expect(visible.map((item) => item.label)).toContain("Animation");
  });

  it("keeps viewers read-only", () => {
    expect(canEdit("VIEWER")).toBe(false);
    expect(canEdit("EDITOR")).toBe(true);
    expect(canEdit("ADMIN")).toBe(true);
  });
});
