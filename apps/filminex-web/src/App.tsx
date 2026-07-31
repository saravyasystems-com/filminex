import { useMemo, useState } from "react";
import {
  canEdit,
  hierarchy,
  navigation,
  visibleNavigation,
  type HierarchyItem,
  type WorkspaceContext
} from "./workspace";
import "./styles.css";

const context: WorkspaceContext = {
  workspaceName: "Saravya Pictures",
  projectName: "The Last Monsoon",
  role: "EDITOR",
  capabilities: new Set(["core", "ai"])
};

const Icon = ({ name }: { name: string }) => (
  <span className="icon" aria-hidden="true">
    {name.slice(0, 1)}
  </span>
);

function App() {
  const [selectedId, setSelectedId] = useState("scene-1");
  const [activeNav, setActiveNav] = useState("story");
  const selected = useMemo(
    () => hierarchy.find((item) => item.id === selectedId) ?? hierarchy[0],
    [selectedId]
  );
  const items = visibleNavigation(navigation, context.capabilities);

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="/" aria-label="Filminex home">
          <span className="brand-mark">F</span>
          <span>
            <strong>Filminex</strong>
            <small>The Filmmaking Workspace</small>
          </span>
        </a>
        <div className="project-context">
          <span>{context.workspaceName}</span>
          <b>/</b>
          <strong>{context.projectName}</strong>
        </div>
        <div className="profile" aria-label={`Current role: ${context.role}`}>
          <span>RS</span>
          <div>
            <strong>Rasika</strong>
            <small>{context.role.toLowerCase()}</small>
          </div>
        </div>
      </header>

      <aside className="rail" aria-label="Workspace navigation">
        <nav>
          {items.map((item) => (
            <button
              className={activeNav === item.id ? "active" : ""}
              key={item.id}
              onClick={() => setActiveNav(item.id)}
              type="button"
            >
              <Icon name={item.label} />
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <p className="principle">
          Only tools available in this workspace are shown.
        </p>
      </aside>

      <main>
        <section className="canvas" aria-labelledby="workspace-title">
          <div className="canvas-heading">
            <div>
              <p className="eyebrow">Story workspace</p>
              <h1 id="workspace-title">{context.projectName}</h1>
              <p>Move through the production hierarchy without leaving context.</p>
            </div>
            <button className="primary" disabled={!canEdit(context.role)}>
              + Add scene
            </button>
          </div>

          <div className="summary-grid">
            <article>
              <small>Structure</small>
              <strong>1 episode · 6 scenes</strong>
            </article>
            <article>
              <small>Production</small>
              <strong>Real + Hybrid</strong>
            </article>
            <article>
              <small>Readiness</small>
              <strong>Story development</strong>
            </article>
          </div>

          <div className="hierarchy">
            <div className="hierarchy-header">
              <h2>Production hierarchy</h2>
              <span>Select an item to inspect it</span>
            </div>
            {hierarchy.map((item, index) => (
              <HierarchyRow
                item={item}
                key={item.id}
                level={index}
                selected={item.id === selectedId}
                onSelect={setSelectedId}
              />
            ))}
          </div>
        </section>

        <Inspector item={selected} editable={canEdit(context.role)} />
      </main>
    </div>
  );
}

function HierarchyRow({
  item,
  level,
  selected,
  onSelect
}: {
  item: HierarchyItem;
  level: number;
  selected: boolean;
  onSelect: (id: string) => void;
}) {
  return (
    <button
      className={`hierarchy-row ${selected ? "selected" : ""}`}
      onClick={() => onSelect(item.id)}
      style={{ "--level": level } as React.CSSProperties}
      type="button"
    >
      <span className="node">{level === 3 ? "•" : "⌄"}</span>
      <span className="row-copy">
        <small>{item.kind}</small>
        <strong>{item.title}</strong>
        <span>{item.detail}</span>
      </span>
      {item.mode && <em>{item.mode}</em>}
    </button>
  );
}

function Inspector({
  item,
  editable
}: {
  item: HierarchyItem;
  editable: boolean;
}) {
  return (
    <aside className="inspector" aria-label="Context inspector">
      <div className="inspector-heading">
        <div>
          <p className="eyebrow">Context inspector</p>
          <h2>{item.title}</h2>
        </div>
        <span className="kind">{item.kind}</span>
      </div>

      <div className="property">
        <label>Production mode</label>
        <strong>{item.mode ?? "Inherited from project"}</strong>
        <p><span className="info">i</span> Project default · explain source</p>
      </div>
      <div className="property">
        <label>Location</label>
        <strong>{item.kind === "Scene" ? "Ancestral home" : "Not set in this context"}</strong>
      </div>
      <div className="property">
        <label>Director note</label>
        <strong>Let the stillness carry the first beat.</strong>
        <p>Declared on this {item.kind.toLowerCase()}</p>
      </div>

      <button className="secondary" disabled={!editable} type="button">
        {editable ? "Edit properties" : "Read only"}
      </button>
      <p className="inspector-note">
        Advanced controls appear only when the selected context requires them.
      </p>
    </aside>
  );
}

export default App;
