export type Capability = "core" | "ai" | "animation";

export type WorkspaceContext = {
  workspaceName: string;
  projectName: string;
  role: "VIEWER" | "EDITOR" | "ADMIN";
  capabilities: ReadonlySet<Capability>;
};

export type NavigationItem = {
  id: string;
  label: string;
  capability: Capability;
};

export type HierarchyItem = {
  id: string;
  kind: "Story" | "Episode" | "Scene" | "Shot";
  title: string;
  detail: string;
  mode?: "Real" | "AI" | "Hybrid" | "Animation";
};

export const navigation: readonly NavigationItem[] = [
  { id: "story", label: "Story", capability: "core" },
  { id: "characters", label: "Characters", capability: "core" },
  { id: "locations", label: "Locations", capability: "core" },
  { id: "ai-studio", label: "AI Studio", capability: "ai" },
  { id: "animation", label: "Animation", capability: "animation" }
];

export const visibleNavigation = (
  items: readonly NavigationItem[],
  capabilities: ReadonlySet<Capability>
): readonly NavigationItem[] =>
  items.filter((item) => capabilities.has(item.capability));

export const canEdit = (role: WorkspaceContext["role"]): boolean =>
  role === "EDITOR" || role === "ADMIN";

export const hierarchy: readonly HierarchyItem[] = [
  {
    id: "story",
    kind: "Story",
    title: "The Last Monsoon",
    detail: "A family drama about memory, place, and a long-delayed return."
  },
  {
    id: "episode-1",
    kind: "Episode",
    title: "Episode 1 · Homecoming",
    detail: "24 minutes · 6 scenes"
  },
  {
    id: "scene-1",
    kind: "Scene",
    title: "The courtyard",
    detail: "Evening · ancestral home",
    mode: "Hybrid"
  },
  {
    id: "shot-1",
    kind: "Shot",
    title: "Arrival at the gate",
    detail: "Wide establishing shot · 8 seconds",
    mode: "Real"
  }
];
