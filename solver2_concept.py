eq_help = """\
1: vavg = (vf + vi) / 2
2: vavg = dx / dt
3: aavg = dv / dt
4: dx = (vi * dt) + (0.5 * a * dt^2)
5: vf^2 = vi ^ 2 + (2a * dx)
"""

inp = input("enter your variables: ")
if inp == "equations": print(eq_help); quit()
# inp = "aavg=5.0 m/s^2,dv=30 m/s,dt=?"
vars = [[a.strip() for a in v.strip().split("=")] for v in inp.split(",")]
pvars = {v[0]: (v[1].split() if v[1] != "?" else None) for v in vars}
pvars_nounits = {v[0]: (float(v[1].split()[0]) if v[1] != "?" else None) for v in vars}

valid_units = {
    "vavg": "m/s",
    "vf": "m/s",
    "vi": "m/s",
    "dv": "m/s",
    "dx": "m",
    "dt": "s",
    "aavg": "m/s^2",
    "a": "m/s^2",
}

unknown = None
for var, val in pvars.items():
    if val is None and unknown is None:
        if unknown is not None:
            print("Too many unknowns. Solving for one unknown at most.")
            quit()    
        unknown = var
    else:
        # validate units
        units = val[1]
        if var in valid_units:
            if acceptable := valid_units[var] != val[1]:
                print(f"Invalid units for {var}. Acceptable units are {acceptable}.")
                quit()
        else:
            print(f"Unknown variable {var}. Supported variables: {list(valid_units.keys())}")
            quit()

if unknown is None:
    print("No unknowns given (nothing to solve for)")
    quit()

sorted_vars = list(pvars.keys())
sorted_vars.sort()

from math import sqrt

solvers = [
    {
        "vavg": lambda pvars: (pvars["vf"] + pvars["vi"]) / 2,
        "vf": lambda pvars: 2 * pvars["vavg"] - pvars["vi"],
        "vi": lambda pvars: 2 * pvars["vavg"] - pvars["vf"]
    },
    {
        "vavg": lambda pvars: (pvars["dx"] / pvars["dt"]),
        "dx": lambda pvars: (pvars["dt"] * pvars["vavg"]),
        "dt": lambda pvars: pvars["dx"] / pvars["vavg"]
    },
    {
        "aavg": lambda pvars: pvars["dv"] / pvars["dt"],
        "dv": lambda pvars: pvars["aavg"] * pvars["dt"],
        "dt": lambda pvars: pvars["dv"] / pvars["aavg"]
    },
    {
        "dx": lambda pvars: (pvars["vi"] * pvars["dt"]) + (0.5 * pvars["a"] * pvars["dt"]**2),
        "vi": lambda pvars: (pvars["dx"] - (0.5 * pvars["a"] * pvars["dt"]^2)) / pvars["dt"],
        "dt": lambda pvars: ((-pvars["vi"] + sqrt(pvars["vi"]**2 + (2 * pvars["a"] * pvars["dx"]))) / pvars["a"], (-pvars["vi"] - sqrt(pvars["vi"]**2 + (2 * pvars["a"] * pvars["dx"]))) / pvars["a"]),
        "a": lambda pvars: (pvars["dx"] - (pvars["vi"] * pvars["dt"])) / (0.5 * pvars["dt"]**2),
    },
    {
        "vf": lambda pvars: sqrt(pvars["vi"]**2 + (2 * pvars["a"] * pvars["dx"])),
        "vi": lambda pvars: sqrt(pvars["vf"]**2 - (2 * pvars["a"] * pvars["dx"])),
        "a": lambda pvars: (pvars["vf"]**2 - pvars["vi"]**2) / 2 * pvars["dx"],
        "dx": lambda pvars: (pvars["vf"]**2 - pvars["vi"]**2) / 2 * pvars["a"],
    },
]

solver_eqs = [
    {
        "vavg": "(vf + vi) / 2",
        "vf": "2 * vavg - vi",
        "vi": "2 * vavg - vf"
    },
    {
        "vavg": "(dx) / (dt)",
        "dx": "dt * vavg",
        "dt": "(dx) / (vavg)"
    },
    {
        "aavg": "d(v / (dt)",
        "dv": "aavg * dt",
        "dt": "(dv) / (aavg)"
    },
    {
        "dx": "(vi * dt) + (0.5 * a * dt^2)",
        "vi": "(dx - (0.5 * a * dt^2)) / dt",
        "dt": "(-vi + sqrt(vi^2 + (2 * a * dx))) / a",
        "a": "(dx - (vi * dt)) / (0.5 * dt^2)",
    },
    {
        "vf": "sqrt(vi^2 + (2 * a * dx))",
        "vi": "sqrt(vf^2 - (2 * a * dx))",
        "a": "(vf^2 - vi^2) / (2 * dx)",
        "dx": "(vf^2 - vi^2) / (2 * a)",
    },
]

eq_idx = 0
if sorted_vars == ["vavg", "vf", "vi"]:
    eq_idx = 0
elif sorted_vars == ["dt", "dx", "vavg"]:
    eq_idx = 1
elif sorted_vars == ["aavg", "dt", "dv"]:
    eq_idx = 2
elif sorted_vars == ["a", "dt", "dx", "vi"]:
    eq_idx = 3
elif sorted_vars == ["a", "dx", "vf", "vi"]:
    eq_idx = 4
else:
    print(f"No known equation found from variables. Supported equations:\n{eq_help}")
    quit()

def format_num(n):
    return f"{'+' if n > 0 else ''}{round(n, 3)}"

eq = solver_eqs[eq_idx][unknown]
print(f"solving equation: \n{unknown} = {eq}")

for var, val in dict(sorted(pvars.items(), key = lambda item: len(item[0]), reverse=True)).items():
    if val is not None:
        eq = eq.replace(var, ' '.join(val))
print(f"{unknown} = {eq}")

result = tuple(solvers[eq_idx][unknown](pvars_nounits))
print(f"{unknown} = {", ".join([format_num(r) for r in result])}{valid_units[unknown]}")
